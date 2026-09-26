package net.ap12.admintool.compat

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.compat.impl.ViaHook
import org.slf4j.LoggerFactory

class HookManager(private val plugin: AdminToolMod) {
    private val logger = LoggerFactory.getLogger("AdminTool/Hook")
    private val initializedHooks = mutableListOf<IHook<*>>()

    fun init() {
        logger.info("Initializing plugin hooks...")

        register(ViaHook())
        // register(TabTpsHook())
        // register(VaultHook())

        logger.info("Enabled ${initializedHooks.size} hooks.")
    }

    private fun <T : IHook<*>> register(hook: T) {
        if (hook.init(plugin)) {
            initializedHooks.add(hook)
            logger.info("Enabled ${hook.name} hook.")
        } else {
            logger.info("Skipped ${hook.name} hook.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : IHook<*>> get(name: String): T? = initializedHooks.find { it.name == name } as? T
}
