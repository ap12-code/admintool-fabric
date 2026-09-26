package net.ap12.admintool.fabric.ui.module

import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.util.inventory.ItemBuilder
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed

interface BaseUIModule : Keyed {
    val id: Key

    override fun key(): Key = id

    fun create(holder: AdminToolUIHolder): ItemBuilder

    fun isEnabled(holder: AdminToolUIHolder): Boolean = true

    fun hasPermission(holder: AdminToolUIHolder): Boolean
}
