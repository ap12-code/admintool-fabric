package net.ap12.admintool.fabric.ui

import com.google.common.collect.ImmutableMultimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.ap12.admintool.fabric.AdminToolMod
import net.ap12.admintool.fabric.util.components.toComponent
import net.minecraft.core.component.DataComponents
import net.minecraft.server.players.NameAndId
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile
import java.net.URI
import java.util.*
import kotlin.jvm.optionals.getOrNull

class HeadManager(private val plugin: AdminToolMod) : IHeadManager {
    private val registeredHeads = mutableMapOf<String, ItemStack>()

    private fun createProfile(name: String): GameProfile? {
        return plugin.server.services().profileResolver().fetchByName(name).getOrNull()
    }

    private fun createHead(name: String, urlStr: String): ItemStack {
        val url = URI.create(urlStr).toURL()
        val stack = ItemStack(Items.PLAYER_HEAD)

        val profile = createProfile(name) ?: return createUnknown()
        val properties =
            ImmutableMultimap.of(
                "textures",
                Property(
                    "textures",
                    Base64.getEncoder()
                        .encodeToString(
                            "{\"textures\":{\"SKIN\":{\"url\":\"${urlStr}\"}}}".encodeToByteArray()
                        ),
                ),
            )
        stack.set(
            DataComponents.PROFILE,
            ResolvableProfile.createResolved(
                GameProfile(UUID.randomUUID(), name, PropertyMap(properties))
            ),
        )

        return stack
    }

    fun createHead(profile: GameProfile): ItemStack {
        val stack = ItemStack(Items.PLAYER_HEAD)
        stack.set(DataComponents.PROFILE, ResolvableProfile.createResolved(profile))

        return stack
    }

    fun createHead(profile: NameAndId): ItemStack {
        val stack = ItemStack(Items.PLAYER_HEAD)
        stack.set(DataComponents.PROFILE, ResolvableProfile.createUnresolved(profile.id))

        return stack
    }

    fun createHead(profile: UUID, item: (ItemStack, GameProfile) -> Unit) {
        ResolvableProfile.createUnresolved(profile)
            .resolveProfile(plugin.server.services().profileResolver())
            .thenAccept { item.invoke(createHead(it), it) }
    }

    private fun register(name: String, url: String) {
        check(!registeredHeads.containsKey(name))
        registeredHeads[name] = createHead(name, url)
    }

    private fun createUnknown(): ItemStack =
        ItemStack(Items.BARRIER).apply {
            set(DataComponents.ITEM_NAME, "&cUnknown Head".toComponent())
        }

    override fun get(name: String): ItemStack = registeredHeads[name]?.copy() ?: createUnknown()

    override fun rebuild() {
        registeredHeads.clear()
        init()
    }

    fun init() {
        plugin.slogger.info("Fetching textured head items...")

        register(
            "checkmark",
            "https://textures.minecraft.net/texture/b5a3b49beec3ab23ae0b60dab56e9cc8fa16769a25830b5d8d6c46378f54430",
        )
        register(
            "next",
            "https://textures.minecraft.net/texture/19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf",
        )
        register(
            "prev",
            "https://textures.minecraft.net/texture/bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9",
        )
        register(
            "trash",
            "https://textures.minecraft.net/texture/be0fd10199e8e4fcdabcae4f85c85918127a7c5553ad235f01c56d18bb9470d3",
        )

        plugin.slogger.info("Successfully fetched ${registeredHeads.size} textures.")
    }
}
