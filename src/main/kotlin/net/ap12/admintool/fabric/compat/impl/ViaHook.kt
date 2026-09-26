package net.ap12.admintool.fabric.compat.impl

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.ViaAPI
import net.ap12.admintool.compat.IHook
import net.ap12.admintool.fabric.AdminToolMod
import net.minecraft.server.level.ServerPlayer

class ViaHook : IHook<ViaAPI<ServerPlayer>> {
    override val name: String = "ViaVersion"

    override fun init(plugin: AdminToolMod): Boolean {
        return Via.isLoaded()
    }

    override fun isEnabled(): Boolean = Via.isLoaded()

    @Suppress("UNCHECKED_CAST")
    override fun getInstance(): ViaAPI<ServerPlayer>? = Via.getAPI() as? ViaAPI<ServerPlayer>
}
