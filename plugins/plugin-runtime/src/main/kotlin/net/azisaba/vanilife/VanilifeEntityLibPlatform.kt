package net.azisaba.vanilife.runtime

import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.atomic.AtomicInteger

// Since 26.2 entity ids are allocated per world, and the stock platform borrows the first world's counter,
// which can hand out ids already used by real entities in other worlds (e.g. the islands world).
// Packet-only entities therefore take ids from a range the per-world counters never reach.
internal class VanilifeEntityLibPlatform(plugin: JavaPlugin) : SpigotEntityLibPlatform(plugin) {
    private val nextId: AtomicInteger = AtomicInteger(FIRST_ENTITY_ID)

    override fun nextEntityId(): Int = nextId.getAndIncrement()

    private companion object {
        const val FIRST_ENTITY_ID: Int = 0x40000000
    }
}
