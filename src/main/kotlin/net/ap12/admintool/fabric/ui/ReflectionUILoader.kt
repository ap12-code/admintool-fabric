package net.ap12.admintool.fabric.ui

import net.ap12.admintool.fabric.AdminToolMod
import org.reflections.Reflections
import org.reflections.scanners.Scanners

class ReflectionUILoader(private val plugin: AdminToolMod) {
    private val reflection = Reflections("net.ap12.admintool.ui.impl", Scanners.SubTypes)

    fun loadAll() {
        reflection.getSubTypesOf(UI::class.java).forEach { plugin.slogger.info(it.name) }
    }
}
