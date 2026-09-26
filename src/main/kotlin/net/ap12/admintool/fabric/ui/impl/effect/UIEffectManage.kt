package net.ap12.admintool.ui.impl.effect

import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.ui.AdminToolUIHolder
import net.ap12.admintool.fabric.ui.UI
import net.ap12.admintool.fabric.util.inventory.UIBuilder
import net.ap12.admintool.fabric.util.inventory.inventory
import net.kyori.adventure.key.Key

class UIEffectManage : UI {
    override val id: Key = AdminToolMod.key("effect_manage")

    override fun create(holder: AdminToolUIHolder): UIBuilder<*> = inventory(holder) {}
}
