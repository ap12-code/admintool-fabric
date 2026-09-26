package net.ap12.admintool.fabric.util.inventory

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.ui.module.BaseUIModule
import net.ap12.admintool.util.isBlankOrNull
import net.ap12.admintool.util.isEmptyOrNull
import net.minecraft.network.chat.Component
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class InventoryBuilder(val holder: AdminToolUIHolder, val ui: UI) : UIBuilder<ContainerWithTitle> {
    val size
        get() = ui.size

    private val container = SimpleContainer(54)

    fun setItem(slot: Int, item: ItemBuilder) {
        this.setItemInternal(slot, item.toItemStack(holder))
    }

    fun setItem(slot: Int, item: BaseUIModule) {
        this.setItem(slot, item.create(holder))
    }

    fun setItem(slot: Int, item: ItemStack) {
        this.setItemInternal(slot, item)
    }

    private fun setItemInternal(slot: Int, item: ItemStack?) {
        if (item.isEmptyOrNull()) {
            container.removeItemNoUpdate(slot)
        } else {
            container.setItem(slot, item!!)
        }

        apply()
    }

    fun get(slot: Int): ItemStack? = container.getSlot(slot)?.get()

    infix fun Int.to(item: ItemBuilder) = setItem(this, item)

    infix fun Int.to(item: ItemStack) = setItem(this, item)

    infix fun Int.to(module: BaseUIModule) = setItem(this, module)

    infix fun Int.to(material: Item) = setItem(this, material.defaultInstance)

    infix fun IntRange.to(item: ItemBuilder) {
        for (i in this) setItem(i, item)
    }

    infix fun IntRange.to(item: ItemStack) {
        for (i in this) setItem(i, item)
    }

    infix fun IntRange.to(module: BaseUIModule) {
        for (i in this) setItem(i, module)
    }

    infix fun IntRange.to(material: Item) {
        for (i in this) setItem(i, material.defaultInstance)
    }

    fun apply() {}

    fun UIBuilder<ContainerWithTitle>.merge() {
        val self = this.build()
        (0..self.containerSize).forEach { index ->
            val stack = self.getSlot(index)?.get()
            if (!stack.isBlankOrNull()) {
                this@InventoryBuilder.setItemInternal(index, stack)
            }
        }
    }

    override fun build(): ContainerWithTitle = ContainerWithTitle(this.getTitle(), container)

    override fun getType() = UIType.INVENTORY

    override fun getTitle(): Component = ui.title
}

fun UI.inventory(
    holder: AdminToolUIHolder,
    builder: InventoryBuilder.() -> Unit,
): UIBuilder<ContainerWithTitle> = InventoryBuilder(holder, this).apply(builder)
