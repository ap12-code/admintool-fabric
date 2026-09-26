package net.ap12.admintool.fabric.permission

import me.lucko.fabric.api.permissions.v0.Permissions
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.minecraft.server.permissions.PermissionLevel

class PermissionManager {

    fun isAllowed(holder: AdminToolUIHolder, actionName: String): Boolean {
        val uiName = holder.currentUI?.key()?.value() ?: ""
        val permissionName = AdminToolMod.permission("actions.${uiName}.${actionName}")

        return Permissions.check(holder.player, permissionName, PermissionLevel.ADMINS)
    }
}
