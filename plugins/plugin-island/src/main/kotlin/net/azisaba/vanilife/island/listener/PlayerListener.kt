package net.azisaba.vanilife.island.listener

import com.github.shynixn.mccoroutine.folia.launch
import net.azisaba.vanilife.island.IslandPlayerMap
import net.azisaba.vanilife.island.cache.IslandCacheMap
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

internal class PlayerListener(private val cacheMap: IslandCacheMap, private val plugin: Plugin) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        plugin.launch {
            val island = cacheMap.lookupOrInit(event.player.uniqueId)
            IslandPlayerMap.put(event.player, island)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.launch {
            IslandPlayerMap.remove(event.player)
        }
    }
}
