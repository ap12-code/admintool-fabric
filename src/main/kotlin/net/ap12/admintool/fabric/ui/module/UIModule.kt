package net.ap12.admintool.fabric.ui.module

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.permission.PermissionState

interface UIModule : BaseUIModule {
    override fun hasPermission(holder: AdminToolUIHolder): Boolean {
        val id = this.key().value()
        val state = holder.plugin.config.permissions.modules[id]
        return PermissionState.test(holder.player, state, "admintool.modules.$id", true)
    }
}
