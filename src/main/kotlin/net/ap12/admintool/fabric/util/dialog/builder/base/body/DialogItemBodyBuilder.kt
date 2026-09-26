package net.ap12.admintool.fabric.util.dialog.builder.base.body

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.dialog.builder.DialogDSL
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.body.ItemBody
import net.minecraft.server.dialog.body.PlainMessage
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.level.ItemLike
import java.util.*

@DialogDSL
@Suppress("unused")
class DialogItemBodyBuilder(private val holder: AdminToolUIHolder) : AbstractDialogBodyBuilder(16) {
    private var item: ItemStack = ItemStack.EMPTY
    private var description: PlainMessage? = null
    private var showDecorations: Boolean = true
    private var showTooltip: Boolean = true
    private var height: Int = 16

    fun item(item: ItemStack) {
        this.item = item
    }

    fun item(item: ItemStack, builder: ItemBuilder.() -> Unit) {
        this.item = ItemBuilder(item).apply(builder).toItemStack(holder)
    }

    fun item(item: ItemLike, builder: ItemBuilder.() -> Unit) = item(ItemStack(item), builder)

    fun description(builder: DialogPlainMessageBodyBuilder.() -> Unit) {
        this.description = DialogPlainMessageBodyBuilder().apply(builder).build()
    }

    fun description(component: Component, width: Int = 200) {
        this.description = PlainMessage(component, width)
    }

    fun showDecorations(value: Boolean) {
        this.showDecorations = value
    }

    fun showTooltip(value: Boolean) {
        this.showTooltip = value
    }

    override fun build(): ItemBody {
        return ItemBody(
            ItemStackTemplate.fromStack(item),
            Optional.ofNullable(description),
            showDecorations,
            showTooltip,
            width,
            height,
        )
    }
}
