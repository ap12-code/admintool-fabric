package net.ap12.admintool.fabric.ui

import eu.pb4.sgui.api.ClickType
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.translate
import net.ap12.admintool.fabric.io.AdminToolPlayerData
import net.ap12.admintool.fabric.ui.tab.Tab
import net.ap12.admintool.fabric.util.Callback
import net.ap12.admintool.fabric.util.LOWER_CHARACTERS
import net.ap12.admintool.fabric.util.Runnable
import net.ap12.admintool.fabric.util.UNDERSCORE
import net.ap12.admintool.fabric.util.dialog.DialogResponse
import net.ap12.admintool.fabric.util.dialog.builder.DialogCallback
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.ItemAction
import net.ap12.admintool.fabric.util.inventory.ItemClickContext
import net.ap12.admintool.fabric.util.inventory.getAction
import net.ap12.admintool.io.AdminToolPublicData
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket
import net.minecraft.server.dialog.Dialog
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

class AdminToolUIHolder(private val context: AdminToolContext, ui: UI) :
    AbstractContainerMenu(MenuType.GENERIC_9x6, context.containerId) {
    val player
        get() = context.player

    val plugin
        get() = context.plugin

    val access
        get() = context.player.registryAccess()

    val pageStore: MutableMap<Key, Int> = mutableMapOf()
    val playerStore: AdminToolPlayerData
        get() = plugin.dataStore.get(context.player.nameAndId())

    val publicStore: AdminToolPublicData
        get() = plugin.publicDataStore.get()

    private val keyStore = mutableMapOf<Key, Key>()
    private var lastInteractTime: Long? = null

    private val tasks = mutableListOf<Runnable>()

    var currentUI: UI? = null

    private val actionMap = mutableMapOf<String, ItemAction>()
    private val dialogActionMap = mutableMapOf<Key, DialogCallback>()

    private var previous = mutableListOf<UI>()
    private var previousCallback = mutableMapOf<UI, Callback<Any?>>()

    val hasPrevious
        get() = previous.isNotEmpty()

    val key
        get() = currentUI?.key()

    val elementNameStore: MutableMap<String, Component> = mutableMapOf()

    var error: Component? = null

    fun hasTab(): Boolean = currentUI is AdminToolUI && (currentUI as AdminToolUI).showTabs

    val container = SimpleContainer(54)
    var title: Component = Component.empty()

    fun setupSlot() {
        for (i in 0..53) {
            this.addSlot(AdminToolSlot(this.container, i, i, 0))
        }
        this.addStandardInventorySlots(player.inventory, 0, 0)
    }

    private fun cleanup() {
        stopTasks()
        actionMap.clear()
        dialogActionMap.clear()
        elementNameStore.clear()
        error = null
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> go(to: UI, recordPrevious: Boolean, callback: (value: R) -> Unit = {}) {
        require(currentUI != null)

        pageStore.remove(currentUI!!.key())
        cleanup()

        if (recordPrevious) {
            previous.add(currentUI!!)
            previousCallback[currentUI!!] = callback as Callback<Any?>
        }

        currentUI = to
        update()
    }

    fun changeTab(to: Tab) {
        previous.clear()
        previousCallback.clear()
        go<Unit>(AdminToolUI(to), false)
    }

    fun editStore(edit: AdminToolPlayerData.() -> Unit) {
        plugin.dataStore.edit(player.uuid, edit)
    }

    fun editPublicStore(edit: AdminToolPublicData.() -> Unit) {
        plugin.publicDataStore.edit(edit)
    }

    fun click(
        slotIndex: Int,
        buttonNum: Int,
        containerInput: ContainerInput,
        player: Player,
    ): Boolean {
        val stack = this.container.getItem(slotIndex)
        val action = stack.getAction()
        val clickType = ClickType.toClickType(containerInput, buttonNum, slotIndex)

        this.broadcastChanges()

        player.containerMenu.sendAllDataToRemote()
        if (action != null) {
            this.getAction(action)?.invoke(ItemClickContext(this, slotIndex, clickType))
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> back(callbackValue: R) {
        if (!hasPrevious) return this.close()
        require(currentUI != null)

        val previousUI = previous.removeLast()
        val previousUICallback = previousCallback.remove(previousUI)
        currentUI!!.onBack(this) {
            previousUICallback?.invoke(callbackValue)

            cleanup()
            currentUI = previousUI
            update()
            this.broadcastFullState()
        }
    }

    fun stopTasks() {
        tasks.clear()
    }

    fun registerTask(task: Runnable) {
        tasks.add(task)
    }

    fun close() {
        player.closeContainer()
        onClose()
    }

    fun setAction(name: String, callback: ItemAction) {
        actionMap[name] = callback
    }

    fun getAction(key: String): ItemAction? = actionMap[key]

    fun setDialogAction(key: Key, callback: DialogCallback) {
        dialogActionMap[key] = callback
    }

    fun playSound(sound: Sound) {
        player.playSound(sound)
    }

    fun getDialogAction(key: Key) = dialogActionMap[key]

    fun onClose() {
        currentUI?.onClose(this)
        stopTasks()
    }

    private fun generateKey(): String =
        String(List(10) { (LOWER_CHARACTERS + UNDERSCORE).random() }.toCharArray())

    fun createKey(): Key {
        require(currentUI != null)
        val generatedKey = AdminToolMod.key(generateKey())
        keyStore[currentUI!!.key()] = generatedKey
        return generatedKey
    }

    fun createStringKey(): String = createKey().value()

    fun handleDialogAction(packet: ServerboundCustomClickActionPacket) {
        getDialogAction(packet.id)?.invoke(DialogResponse(this, packet))
        player.connection.send(ClientboundClearDialogPacket.INSTANCE)
    }

    fun update() {
        when (val createdUI = currentUI!!.create(this).build()) {
            is ContainerWithTitle -> {
                createdUI.slots.forEach { (i, slot) -> this.container.setItem(i, slot.item) }
                this.broadcastChanges()
            }
            is Dialog -> {
                player.openDialog(Holder.direct(createdUI))
            }
        }
    }

    fun ifError(consumer: Consumer<Component>) {
        if (error != null) consumer.accept(error!!.translate(this.player))
    }

    fun getCurrent(): UI? {
        return currentUI
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player): Boolean {
        return true
    }

    class Provider(val plugin: AdminToolMod, val initialUI: UI) : MenuProvider {
        override fun getDisplayName(): Component {
            return this.initialUI.title
        }

        override fun createMenu(
            containerId: Int,
            inventory: Inventory,
            player: Player,
        ): AbstractContainerMenu {
            val context = AdminToolContext(plugin, player as ServerPlayer, containerId)
            return AdminToolUIHolder(context, initialUI)
        }
    }

    init {
        currentUI = ui
        setupSlot()
        update()
    }
}
