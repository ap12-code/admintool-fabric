package net.ap12.admintool.util

import java.util.UUID

fun isValidUUID(uuidStr: String): Boolean {
    return try {
        UUID.fromString(uuidStr)
        true
    } catch (_: IllegalArgumentException) {
        false
    }
}
