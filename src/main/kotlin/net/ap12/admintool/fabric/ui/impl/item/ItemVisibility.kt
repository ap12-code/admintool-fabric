package net.ap12.admintool.ui.impl.item

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import net.ap12.admintool.fabric.config.ext.NbtEnumSerializer
import net.kyori.adventure.translation.Translatable

@Serializable(ItemVisibility.Serializer::class)
enum class ItemVisibility(private val code: Byte) : Translatable {
    PUBLIC(0),
    PRIVATE(1);

    override fun translationKey(): String {
        return "admintool.ui.item.visibility.${this.name.lowercase()}"
    }

    fun inverted() = if (this == PUBLIC) PRIVATE else PUBLIC

    companion object {
        fun fromCode(code: Byte) = requireNotNull(ItemVisibility.entries.find { it.code == code })
    }

    object Serializer :
        NbtEnumSerializer<ItemVisibility, Byte>(
            "ItemVisibility",
            PrimitiveKind.BYTE,
            { it.code },
            { fromCode(it) },
        )
}
