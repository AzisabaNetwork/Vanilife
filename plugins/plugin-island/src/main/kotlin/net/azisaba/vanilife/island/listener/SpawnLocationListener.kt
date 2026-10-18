package net.azisaba.vanilife.island.listener

import com.github.shynixn.mccoroutine.folia.launch
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent
import kotlinx.coroutines.runBlocking
import net.azisaba.vanilife.island.IslandFeature
import net.azisaba.vanilife.island.IslandPlayerMap
import net.azisaba.vanilife.island.IslandSpawnPointFinder
import net.azisaba.vanilife.island.cache.IslandCacheMap
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.Plugin

internal class SpawnLocationListener(
    private val cacheMap: IslandCacheMap,
    private val plugin: Plugin,
) : Listener {
    @EventHandler
    fun onAsyncPlayerSpawnLocation(event: AsyncPlayerSpawnLocationEvent) {
        val playerUuid = event.connection.profile.id ?: return
        runBlocking {
            val island = cacheMap.lookupOrInit(playerUuid)
            val spawnPoint = island.spawnPoint
            event.spawnLocation = if (IslandSpawnPointFinder.isSafe(spawnPoint)) {
                spawnPoint
            } else {
                IslandSpawnPointFinder.find(island)?.let { safeSpawnPoint ->
                    island.spawnPoint(safeSpawnPoint)
                    safeSpawnPoint
                } ?: spawnPoint
            }
        }
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val island = cacheMap.lookupCached(player.uniqueId) ?: return
        event.respawnLocation = island.spawnPoint
        plugin.launch {
            if (IslandPlayerMap.lookup(player.uniqueId) === island) {
                if (island.isEnabled(IslandFeature.FLIGHT)) {
                    player.allowFlight = true
                }
                if (island.isEnabled(IslandFeature.CUSTOM_SKY_COLOR)) {
                    island.sendSkyColor(player)
                }
            } else {
                IslandPlayerMap.put(player, island)
            }
        }
    }
}
