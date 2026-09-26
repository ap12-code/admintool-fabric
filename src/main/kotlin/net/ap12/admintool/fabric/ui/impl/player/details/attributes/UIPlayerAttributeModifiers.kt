package net.ap12.admintool.fabric.ui.impl.player.details.attributes

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.impl.player.details.AbstractUIPlayerDetails
import net.ap12.admintool.fabric.ui.paginator.UISimplePaginator
import net.ap12.admintool.fabric.util.components.toComponent
import net.ap12.admintool.fabric.util.inventory.ContainerWithTitle
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.ap12.admintool.fabric.util.inventory.item
import net.ap12.admintool.fabric.util.with
import net.ap12.admintool.ui.impl.player.details.attributes.UIPlayerAttributeModifierValue
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.Items
import java.text.DecimalFormat
import java.util.*

class UIPlayerAttributeModifiers(target: UUID, val attribute: Holder<Attribute>) :
    AbstractUIPlayerDetails("attribute.modifier", target) {
    private fun buildItem(
        holder: AdminToolUIHolder,
        element: AttributeModifier,
        player: ServerPlayer,
    ) =
        item(Items.BOOK) {
            name(Component.literal(element.id.asString()))
            glint(true)

            lore {
                +field(
                    "admintool.ui.player.details.attribute.modifier.amount",
                    DECIMAL_FORMAT.format(element.amount),
                )
                +field(
                    "admintool.ui.player.details.attribute.modifier.operation",
                    element.operation.name.toComponent(),
                )
                +""
                +action("admintool.ui.player.details.attribute.modifier.edit", Keys.MOUSE_LEFT)
                +action("admintool.ui.player.details.attribute.modifier.remove", Keys.MOUSE_RIGHT)
            }

            onClick(
                "admintool.ui.player.details.attribute.modifier.${element.id.toLanguageKey()}"
            ) { context ->
                if (context.isLeftClick) {
                    context.go<AttributeModifier?>(
                        UIPlayerAttributeModifierValue("edit", attribute, element)
                    ) { result ->
                        if (result != null) {
                            val instance = requireNotNull(player.getAttribute(attribute))
                            instance.removeModifier(element.id)
                            instance.addPermanentModifier(result)
                            context.holder.update()
                        }
                    }
                } else if (context.isRightClick) {
                    val instance = requireNotNull(player.getAttribute(attribute))
                    instance.removeModifier(element.id)
                    context.holder.update()
                }
            }
        }

    private fun getAttributes(
        holder: AdminToolUIHolder,
        player: ServerPlayer,
    ): List<AttributeModifier> {
        return player.getAttribute(attribute)?.modifiers?.toList().orEmpty()
    }

    override fun createChild(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            val player = getPlayer(holder)

            UISimplePaginator(
                    this@UIPlayerAttributeModifiers.id,
                    3,
                    5,
                    with(::getAttributes, player),
                    with(::buildItem, player),
                )
                .create(holder)
                .merge()

            45 to
                item(Items.NAME_TAG) {
                    name("admintool.ui.player.details.attribute.modifier.add")

                    onClick("admintool.ui.player.details.attribute.modifier.add") { context ->
                        context.go<AttributeModifier?>(
                            UIPlayerAttributeModifierValue("add", attribute)
                        ) { result ->
                            if (result != null) {
                                requireNotNull(player.getAttribute(attribute))
                                    .addPermanentModifier(result)
                                context.holder.update()
                            }
                        }
                    }
                }

            8 to
                item(Items.ITEM_FRAME) {
                    name(attribute.value().descriptionId)

                    onClick("admintool.ui.player.details.attribute.modifier.back") { context ->
                        context.back(null)
                    }
                }
        }

    companion object {
        val DECIMAL_FORMAT = DecimalFormat("0.###")
    }
}
