package net.ap12.admintool.fabric.util.inventory

import net.ap12.admintool.fabric.ui.UI
import net.minecraft.network.chat.Component
import net.minecraft.world.Container

class DirectInventoryBuilder(private val inventory: Container, private val title: Component) :
    UIBuilder<Container> {
    override fun build(): Container {
        return inventory
    }

    override fun getType(): UIType {
        return UIType.INVENTORY
    }

    override fun getTitle(): Component {
        return title
    }
}

fun UI.inventory(inventory: Container) = DirectInventoryBuilder(inventory, this.title)
