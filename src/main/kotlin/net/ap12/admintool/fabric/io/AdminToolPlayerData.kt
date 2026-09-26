package net.ap12.admintool.fabric.io

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.ui.impl.item.builder.ItemBuilderPlayerData
import net.ap12.admintool.fabric.ui.impl.item.storage.UIStorage
import net.ap12.admintool.fabric.ui.impl.player.UIPlayer
import net.ap12.admintool.fabric.ui.impl.settings.PlayerOptions
import net.ap12.admintool.fabric.ui.impl.waypoint.editor.WaypointEditorPlayerData
import net.ap12.admintool.io.IAdminToolPlayerData
import net.ap12.admintool.ui.impl.item.UIItem

@Serializable
data class AdminToolPlayerData(
    var waypointEditor: WaypointEditorPlayerData = WaypointEditorPlayerData(),
    var lastTab: String = "home",
    var playerUIData: UIPlayer.Data = UIPlayer.Data(),
    var itemUIData: UIItem.Data = UIItem.Data(),
    var itemBuilder: MutableMap<String, ItemBuilderPlayerData> = mutableMapOf(),
    var storage: UIStorage.Data = UIStorage.Data(),
    var options: PlayerOptions = PlayerOptions(),
) : IAdminToolPlayerData
