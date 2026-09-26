package net.ap12.admintool.fabric.openinv

import net.kyori.adventure.util.TriState
import net.minecraft.world.entity.player.Abilities

class PlayerAbilities(private val player: OfflinePlayer) {
    private val abilities = Abilities()
    private var flyingFallDamage: TriState = TriState.NOT_SET
    private val serverPlayer
        get() = player.serverPlayer

    private var dirty: Boolean = false

    init {
        refresh()
    }

    fun apply(other: PlayerAbilities) {
        abilities.instabuild = other.instaBuild()
        abilities.mayBuild = other.mayBuild()
        abilities.mayfly = other.mayFly()
        abilities.walkingSpeed = other.walkSpeed()
        abilities.flyingSpeed = other.flySpeed()
        flyingFallDamage = other.flyingFallDamage()
        this.markDirty()
    }

    fun refresh() {
        this.abilities.apply(serverPlayer.abilities.pack())
        this.dirty = false
    }

    fun clone(): PlayerAbilities {
        val cloned = PlayerAbilities(player)
        cloned.apply(this)
        return cloned
    }

    fun update() {
        serverPlayer.abilities.apply(this.abilities.pack())
        serverPlayer.onUpdateAbilities()
    }

    fun markDirty() {
        this.dirty = true
    }

    fun isDirty(): Boolean {
        return dirty
    }

    fun instaBuild(): Boolean {
        return abilities.instabuild
    }

    fun instaBuild(instaBuild: Boolean) {
        abilities.instabuild = instaBuild
        this.markDirty()
        update()
    }

    fun mayFly(): Boolean {
        return abilities.mayfly
    }

    fun mayFly(mayFly: Boolean) {
        abilities.mayfly = mayFly
        if (!mayFly && abilities.flying) {
            abilities.flying = false
        }
        this.markDirty()
        update()
    }

    fun mayBuild(): Boolean {
        return abilities.mayBuild
    }

    fun mayBuild(mayBuild: Boolean) {
        abilities.mayBuild = mayBuild
        this.markDirty()
        update()
    }

    fun walkSpeed(): Float {
        return abilities.walkingSpeed
    }

    fun walkSpeed(walkSpeed: Float) {
        abilities.walkingSpeed = walkSpeed
        this.markDirty()
        update()
    }

    fun flySpeed(): Float {
        return abilities.flyingSpeed
    }

    fun flySpeed(flySpeed: Float) {
        abilities.flyingSpeed = flySpeed
        this.markDirty()
        update()
    }

    fun flyingFallDamage(): TriState {
        return flyingFallDamage
    }

    fun flyingFallDamage(fallFlyingDamage: TriState) {
        flyingFallDamage = fallFlyingDamage
        this.markDirty()
        update()
    }
}
