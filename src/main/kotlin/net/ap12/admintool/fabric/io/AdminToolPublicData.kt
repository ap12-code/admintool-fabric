package net.ap12.admintool.io

import kotlinx.serialization.Serializable
import net.ap12.admintool.ui.impl.item.UIItem

@Serializable
data class AdminToolPublicData(var itemUIData: UIItem.Data = UIItem.Data()) : IAdminToolPlayerData
