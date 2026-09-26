package net.ap12.admintool.fabric.util.permission

import com.mojang.brigadier.StringReader
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.level.ServerPlayer
import java.util.function.Predicate

class PermissionParser {
    companion object {
        private val PATTERN = Regex("([a-zA-Z1-9]+\\.?)+")
    }

    fun parse(input: String): PermissionState {
        val reader = StringReader(input)
        var isOr = false
        var isAnd = false
        var predicate: Predicate<ServerPlayer> = { false }

        while (reader.canRead()) {
            reader.skipWhitespace()
            if (reader.peek() == '&' && reader.peek(1) == '&') {
                if (isOr) throw IllegalArgumentException("duplicate condition syntax")
                reader.skip()
                reader.skip()
                isAnd = true
            } else if (reader.peek() == '|' && reader.peek(1) == '|') {
                if (isAnd) throw IllegalArgumentException("duplicate condition syntax")
                reader.skip()
                reader.skip()
                isOr = true
            } else {
                val rawNode = reader.readString()
                val inverted = rawNode.startsWith("!")
                val node = rawNode.removePrefix("!")

                if (!PATTERN.matches(node))
                    throw IllegalArgumentException("invalid permission node: $rawNode")

                if (isOr) {
                    predicate = predicate.or {
                        Permissions.check(it, node).let { bool -> if (inverted) !bool else bool }
                    }
                    isOr = false
                }
                if (isAnd) {
                    predicate = predicate.and {
                        Permissions.check(it, node).let { bool -> if (inverted) !bool else bool }
                    }
                    isAnd = false
                }
            }
            reader.skipWhitespace()
        }

        return PermissionState(predicate, input)
    }
}
