package net.ap12.admintool.fabric.ui.impl.player.details.attributes

import net.ap12.admintool.fabric.i18n.t
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
import net.ap12.admintool.ui.impl.player.details.attributes.UIPlayerAttributeValue
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.item.Items
import java.text.DecimalFormat
import java.util.*

class UIPlayerAttribute(target: UUID) : AbstractUIPlayerDetails("attribute", target) {
    private fun formatValue(instance: AttributeInstance): Component {
        val baseValue = instance.baseValue
        val modifierValue = calculateAmount(instance) - baseValue
        val format = DecimalFormat("0.0##")

        return "&a${format.format(instance.value)} &7(${format.format(baseValue)} + ${format.format(modifierValue)})"
            .toComponent()
    }

    private fun buildItem(
        holder: AdminToolUIHolder,
        element: Holder<Attribute>,
        player: ServerPlayer,
    ) =
        item(Items.ITEM_FRAME) {
            name(
                t(element.value().descriptionId)
                    .append(" ")
                    .append(
                        Component.translatable(element.value().descriptionId)
                            .withColor(TextColor.GRAY)
                    )
            )

            val attribute = requireNotNull(player.getAttribute(element))
            lore {
                +field("admintool.ui.current", formatValue(attribute))
                +""
                +action("admintool.ui.player.details.attribute.edit.modifier", Keys.MOUSE_LEFT)
                +action("admintool.ui.player.details.attribute.edit.base", Keys.MOUSE_RIGHT)
                +action("admintool.ui.player.details.attribute.edit.base.reset", Keys.MOUSE_MIDDLE)
            }

            onClick("admintool.ui.player.details.attribute.${element.value().descriptionId}") {
                context ->
                if (context.isLeftClick) {
                    context.go<Nothing>(
                        UIPlayerAttributeModifiers(player.uuid, attribute.attribute)
                    )
                } else if (context.isRightClick) {
                    context.go<Double?>(UIPlayerAttributeValue(element, attribute.baseValue)) {
                        result ->
                        if (result != null) attribute.baseValue = result
                        context.holder.update()
                    }
                } else if (context.isMiddleClick) {
                    player.attributes.resetBaseValue(attribute.attribute)
                    context.holder.update()
                }
            }
        }

    private fun getAttributes(
        holder: AdminToolUIHolder,
        player: ServerPlayer,
    ): List<Holder<Attribute>> {
        return BuiltInRegistries.ATTRIBUTE.asHolderIdMap().toList()
    }

    override fun createChild(holder: AdminToolUIHolder): UIBuilder<ContainerWithTitle> =
        inventory(holder) {
            val player = getPlayer(holder)

            UISimplePaginator(
                    this@UIPlayerAttribute.id,
                    3,
                    5,
                    with(::getAttributes, player),
                    with(::buildItem, player),
                )
                .create(holder)
                .merge()
        }
}
