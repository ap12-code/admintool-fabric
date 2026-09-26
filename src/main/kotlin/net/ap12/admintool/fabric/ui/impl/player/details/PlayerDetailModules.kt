package net.ap12.admintool.fabric.ui.impl.player.details

import net.ap12.admintool.fabric.openinv.OfflinePlayer
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.impl.player.details.abilities.UIPlayerAbilities
import net.ap12.admintool.fabric.ui.impl.player.details.attributes.UIPlayerAttribute
import net.ap12.admintool.fabric.util.inventory.InventoryBuilder
import net.ap12.admintool.fabric.util.inventory.item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class PlayerDetailModules(
    private val target: OfflinePlayer,
    private val holder: AdminToolUIHolder,
) {
    private val teleport =
        item(Items.ENDER_PEARL) {
            name("admintool.ui.player.details.teleport")

            lore {
                +""
                +action("admintool.ui.player.details.teleport.teleport", Keys.CLICK)
            }

            onClick("admintool.ui.player.details.teleport") { context ->
                val teleportMode = holder.playerStore.options.teleportSpectatorMode
                teleportMode.teleport(context.player, target)
            }
        }

    private val inventory =
        item(Items.CHEST) {
            name("admintool.ui.player.details.inventory")

            lore {
                +""
                +action("admintool.ui.player.details.inventory.open", Keys.CLICK)
            }

            onClick("admintool.ui.player.details.inventory") { context ->
                val opened = context.go<Nothing>(UIPlayerInventory())
                holder.plugin.openInv.createInventory(target, holder)
            }
        }

    private val enderChest =
        item(Items.ENDER_CHEST) {
            name("admintool.ui.player.details.ender_chest")

            lore {
                +""
                +action("admintool.ui.player.details.ender_chest.open", Keys.CLICK)
            }

            onClick("admintool.ui.player.details.ender_chest") { context ->
                val opened = context.go<Nothing>(UIPlayerEnderChest())
                holder.plugin.openInv.createEnderChest(target, holder)
            }
        }

    private val abilities =
        item(Items.GOLD_INGOT) {
            name("admintool.ui.player.details.abilities")

            lore {
                +""
                +action("admintool.ui.player.details.abilities.edit", Keys.CLICK)
            }

            onClick("admintool.ui.player.details.abilities") { context ->
                context.go<Nothing>(UIPlayerAbilities(target))
            }
        }

    private val attributes =
        item(Items.IRON_INGOT) {
            name("admintool.ui.player.details.attribute")

            lore {
                +""
                +action("admintool.ui.player.details.attribute.edit", Keys.CLICK)
            }

            onClick("admintool.ui.player.details.attribute") { context ->
                context.go<Nothing>(UIPlayerAttribute(target.getUUID()))
            }
        }

    private val headStack = holder.plugin.heads.createHead(target.nameAndId())
    private val head =
        item(headStack.copy()) {
            name("admintool.ui.player.details.head")

            lore {
                +""
                +action("admintool.ui.player.details.head.get", Keys.MOUSE_LEFT)
                +action(
                    "admintool.ui.player.details.head.get.stack",
                    Keys.MOUSE_LEFT,
                    Keys.KEY_SHIFT,
                )
            }

            onClick("admintool.ui.player.details.get_head") { context ->
                val amount = if (context.isShift) 64 else 1
                if (context.cursor.isEmpty) {
                    context.cursor = headStack.copyWithCount(amount)
                } else {
                    context.cursor = ItemStack.EMPTY
                }
            }
        }

    private val modules = listOf(teleport, inventory, enderChest, abilities, attributes, head)

    fun apply(inventoryBuilder: InventoryBuilder, startIndex: Int) {
        modules.forEachIndexed { index, builder ->
            inventoryBuilder.setItem(startIndex + index, builder)
        }
    }
}
