package net.ap12.admintool.fabric.ui.impl.player

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.Keys
import net.ap12.admintool.fabric.ui.Sounds
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.inventory.*
import net.kyori.adventure.key.Key
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.Items

class UIPlayerFilter : UI {
    override val id: Key = AdminToolMod.key("player.filter")

    private fun updateData(
        context: ItemClickContext,
        isOffline: Boolean? = null,
        isOnline: Boolean? = null,
        isOp: Boolean? = null,
    ) {
        context.holder.editStore {
            if (isOffline != null) playerUIData.filter.offline = isOffline
            if (isOp != null) playerUIData.filter.op = isOp
            if (isOnline != null) playerUIData.filter.online = isOnline
        }
        context.holder.update()
    }

    private fun createFilter(key: String, state: Boolean, callback: ItemAction): ItemBuilder {
        val material = if (state) Items.CONCRETE.lime else Items.CONCRETE.red
        return item(material) {
            name("admintool.ui.player.filter.$key")

            lore {
                if (state) {
                    +field(t("admintool.value.state.on"), TextColor.GREEN)
                } else {
                    +field(t("admintool.value.state.off"), TextColor.RED)
                }
                +""
                +action("admintool.ui.player.filter.switch", Keys.CLICK)
            }

            onClick("admintool.ui.player.filter.$key") { context ->
                context.playSound(Sounds.click2)
                callback(context)
            }
        }
    }

    override fun create(holder: AdminToolUIHolder) =
        inventory(holder) {
            val data = holder.playerStore.playerUIData.filter

            0 to
                createFilter("offline", !data.offline) { context ->
                    updateData(context, isOffline = !data.offline)
                }
            1 to
                createFilter("online", !data.online) { context ->
                    updateData(context, isOnline = !data.online)
                }
            2 to createFilter("op", !data.op) { context -> updateData(context, isOp = !data.op) }
        }
}
