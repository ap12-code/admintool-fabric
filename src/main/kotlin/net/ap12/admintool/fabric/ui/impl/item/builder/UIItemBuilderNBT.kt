package net.ap12.admintool.fabric.ui.impl.item.builder

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.dialog.dialog
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.removeActionData
import net.kyori.adventure.key.Key
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.ItemStack
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.nio.charset.StandardCharsets

class UIItemBuilderNBT(private val stack: ItemStack) : UI {
    override val id: Key = AdminToolMod.key("item.builder.nbt")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> =
        dialog(holder) {
            val nbt = holder.createStringKey()
            val nbtStr =
                NbtUtils.toPrettyComponent(
                        ItemStack.CODEC.encodeStart(
                                NbtOps.INSTANCE,
                                stack.copy().removeActionData(),
                            )
                            .orThrow
                    )
                    .string

            base {
                title("admintool.ui.item.builder.nbt")
                textInput(nbt) {
                    labelVisible(false)
                    initial(nbtStr)
                    maxLength(Int.MAX_VALUE)
                    width(800)
                    multiline { height(512) }
                }
            }
            type {
                confirmation {
                    yes {
                        label("admintool.ui.confirm")
                        action { response ->
                            try {
                                val stack =
                                    NbtIo.read(
                                        DataInputStream(
                                            ByteArrayInputStream(
                                                response
                                                    .getString(nbt)
                                                    .toByteArray(StandardCharsets.UTF_8)
                                            )
                                        )
                                    )
                                response.holder.back(stack)
                            } catch (e: Exception) {
                                response.validationFailed(
                                    Component.literal(e.message.orEmpty()).withColor(TextColor.RED)
                                )
                            }
                        }
                    }
                    no {
                        label("admintool.ui.cancel")
                        action { response -> response.holder.back(null) }
                    }
                }
            }
        }
}
