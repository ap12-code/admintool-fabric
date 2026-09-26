@file:Suppress("UnstableApiUsage")

package net.ap12.admintool.fabric.util

import net.minecraft.core.HolderSet
import kotlin.jvm.optionals.getOrNull

fun <T : Any> HolderSet<T>.format(limit: Int? = 5): String {
    return if (this.unwrapKey().getOrNull() != null) {
        "#${this.unwrapKey().getOrNull()?.location}"
    } else {
        this.mapNotNull { it.unwrapKey().getOrNull()?.identifier() }
            .let {
                if (limit != null) if (it.size > limit) it.take(limit).plus(" ...") else it else it
            }
            .joinToString(",")
    }
}
