package net.ap12.admintool.fabric.util.permission

import kotlinx.serialization.Serializable
import net.ap12.admintool.fabric.config.ext.PermissionSerializer
import net.ap12.admintool.fabric.util.hasPermission
import net.minecraft.server.level.ServerPlayer
import java.util.function.Predicate

@Serializable(PermissionSerializer::class)
class PermissionState(private val predicate: Predicate<ServerPlayer>, val condition: String) {
    fun test(permissible: ServerPlayer): Boolean {
        return predicate.test(permissible)
    }

    companion object {
        fun test(
            target: ServerPlayer,
            state: PermissionState?,
            defaultNode: String? = null,
            defaultOp: Boolean = false,
        ): Boolean {
            return state?.test(target) == true ||
                (defaultNode != null && target.hasPermission(defaultNode)) ||
                (defaultOp && target.level().server.playerList.isOp(target.nameAndId()))
        }
    }
}
