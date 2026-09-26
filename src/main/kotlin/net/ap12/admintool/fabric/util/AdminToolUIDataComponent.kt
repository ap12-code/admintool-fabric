package net.ap12.admintool.fabric.util

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

class AdminToolUIDataComponent(val action: String, val cancel: Boolean, val placeholder: Boolean) {
    companion object {
        val DEFAULT = AdminToolUIDataComponent("", true, placeholder = true)

        val CODEC: Codec<AdminToolUIDataComponent> = RecordCodecBuilder.create { instance ->
            instance
                .group(
                    Codec.STRING.fieldOf("action").forGetter(AdminToolUIDataComponent::action),
                    Codec.BOOL.fieldOf("cancel").forGetter(AdminToolUIDataComponent::cancel),
                    Codec.BOOL.fieldOf("placeholder")
                        .forGetter(AdminToolUIDataComponent::placeholder),
                )
                .apply(instance, ::AdminToolUIDataComponent)
        }
    }
}
