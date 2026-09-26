package net.ap12.admintool.fabric.ui

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

class Sounds {
    companion object {
        private val soundSource =
            if (Sound.Source.entries.map { it.name.lowercase() }.contains("ui")) {
                Sound.Source.UI
            } else {
                Sound.Source.MASTER
            }

        val clickDefault
            get() = createSound("minecraft:ui.button.click")

        val next
            get() = createSound("minecraft:ui.button.click", 1.5f)

        val previous
            get() = createSound("minecraft:ui.button.click", 1.25f)

        val click2
            get() = createSound("minecraft:ui.button.click", 2.0f)

        val click3
            get() = createSound("minecraft:entity.experience_orb.pickup", 1.0f)

        fun createSound(key: String, pitch: Float = 1.0f) =
            Sound.sound().type(Key.key(key)).source(soundSource).pitch(pitch).volume(1.0f).build()
    }
}
