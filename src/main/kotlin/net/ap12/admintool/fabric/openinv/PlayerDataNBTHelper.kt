package net.ap12.admintool.fabric.openinv

import com.mojang.serialization.Codec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.world.level.storage.TagValueOutput

class PlayerDataNBTHelper {
    companion object {
        fun <T : Tag> copyValue(
            source: CompoundTag,
            target: CompoundTag,
            container: String,
            key: String,
            tagType: Class<T>,
        ) {
            val sourceContainer = getTag(source, container, CompoundTag::class.java)
            val targetContainer = getTag(target, container, CompoundTag::class.java) ?: return

            setTag(targetContainer, key, getTag(sourceContainer, key, tagType))
        }

        fun <T : Any> copyValue(
            source: CompoundTag,
            target: TagValueOutput,
            container: String,
            key: String,
            codec: Codec<T>,
        ) {
            val sourceContainer = getTag(source, container, CompoundTag::class.java) ?: return
            val targetContainer = target.child(container)
            val sourceTag = sourceContainer.get(key) ?: return

            val sourceData = codec.parse(NbtOps.INSTANCE, sourceTag)
            sourceData.ifSuccess { value -> targetContainer.store(key, codec, value) }
        }

        private fun <T : Tag> getTag(container: CompoundTag?, key: String, dataType: Class<T>): T? {
            val value = container?.get(key)
            if (value == null || !dataType.isAssignableFrom(value::class.java)) {
                return null
            }
            return dataType.cast(value)
        }

        private fun <T : Tag> setTag(container: CompoundTag, key: String, data: T?) {
            if (data == null) {
                container.remove(key)
            } else {
                container.put(key, data)
            }
        }
    }
}
