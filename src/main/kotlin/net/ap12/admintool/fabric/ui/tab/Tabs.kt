package net.ap12.admintool.fabric.ui.tab

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import net.ap12.admintool.fabric.config.ext.NbtEnumSerializer
import net.ap12.admintool.fabric.ui.impl.effect.UIEffect
import net.ap12.admintool.fabric.ui.impl.home.UIHome
import net.ap12.admintool.fabric.ui.impl.player.UIPlayer
import net.ap12.admintool.fabric.ui.impl.waypoint.UITeleport
import net.ap12.admintool.ui.impl.item.UIItem

@Serializable(Tabs.Serializer::class)
enum class Tabs(val code: Byte, val creator: () -> Tab) {
    HOME(0, ::UIHome),
    TELEPORT(1, ::UITeleport),
    PLAYER(2, ::UIPlayer),
    EFFECT(3, ::UIEffect),
    ITEM(4, ::UIItem);

    object Serializer :
        NbtEnumSerializer<Tabs, Byte>("Tabs", PrimitiveKind.BYTE, { it.code }, { fromCode(it) })

    companion object {
        fun fromCode(code: Byte) = entries.find { it.code == code } ?: HOME
    }
}
