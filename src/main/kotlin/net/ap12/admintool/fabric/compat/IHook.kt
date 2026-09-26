package net.ap12.admintool.compat

import net.ap12.admintool.fabric.AdminToolMod

interface IHook<T> {
    val name: String

    fun init(plugin: AdminToolMod): Boolean

    fun isEnabled(): Boolean

    fun getInstance(): T?
}
