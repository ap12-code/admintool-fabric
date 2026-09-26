package net.ap12.admintool.fabric.ui.module

import net.ap12.admintool.fabric.ui.module.impl.*
import net.kyori.adventure.key.Key
import java.util.function.Supplier

class UIModules : Iterable<UIModule> {
    private val modules = mutableListOf<UIModule>()

    init {
        register(::FlyModule)
        register(::HealModule)
        register(::VanishModule)

        registerAll(GamemodeModule::createAllGameModes)
        registerAll(MenuModule.Companion::createAllMenus)
    }

    private fun registerAll(supplier: Supplier<List<UIModule>>) {
        modules.addAll(supplier.get())
    }

    private fun register(supplier: Supplier<UIModule>) {
        modules.add(supplier.get())
    }

    fun getKeys(): List<Key> {
        return modules.map(UIModule::key)
    }

    override fun iterator(): Iterator<UIModule> {
        return modules.iterator()
    }
}
