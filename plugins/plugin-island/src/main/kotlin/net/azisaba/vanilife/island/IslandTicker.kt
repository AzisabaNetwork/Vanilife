package net.azisaba.vanilife.island

import com.github.shynixn.mccoroutine.folia.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.azisaba.vanilife.ConfigurationHolder
import net.azisaba.vanilife.island.cache.IslandCacheMap
import net.azisaba.vanilife.island.leveling.requirements.LevelUpRequirementProvider
import net.azisaba.vanilife.island.leveling.tryLevelUp
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.logging.Level
import kotlin.time.Duration.Companion.milliseconds

internal class IslandTicker(
    private val plugin: Plugin,
    private val cacheMap: IslandCacheMap,
    private val levelUpRequirements: ConfigurationHolder<LevelUpRequirementProvider>,
    private val levelUpCheckIntervalTicks: ConfigurationHolder<Long>,
) : AutoCloseable {
    private val job: Job = plugin.launch {
        var time = 0L
        while (isActive) {
            for (island in cacheMap) {
                // Guard each island so a single failure does not stop ticking for every island
                try {
                    island.tick(time)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    plugin.logger.log(Level.SEVERE, "Failed to tick island at ${island.position}", e)
                }
            }
            time++
            delay(50L.milliseconds)
        }
    }

    override fun close() {
        job.cancel()
    }

    private suspend fun Island.tick(time: Long) {
        val tickLevel = tickLevel()
        if (time % tickLevel.tickInterval == 0L) {
            tickLevel.tick(this, time)
        }

        val levelUpCheckInterval = levelUpCheckIntervalTicks.value().coerceAtLeast(1L)
        if (time % levelUpCheckInterval == 0L && IslandPlayerMap.lookup(owner) === this) {
            tryLevelUp(levelUpRequirements.value(), plugin)
        }
    }

    private fun Island.tickLevel(): TickLevel = when {
        IslandPlayerMap.collect(this).isNotEmpty() -> TickLevel.ACTIVE
        Bukkit.getPlayer(owner) != null -> TickLevel.OWNER_ONLINE
        else -> TickLevel.IDLE
    }

    private enum class TickLevel(val tickInterval: Long) {
        ACTIVE(1L) {
            override suspend fun tick(island: Island, time: Long) {
                if (IslandPlayerMap.lookup(island.owner) === island) {
                    island.wrackTick(time, island.level, enchantments = island)
                }
                island.waveTick(time)
            }
        },
        OWNER_ONLINE(3L) {
            override suspend fun tick(island: Island, time: Long) {
                island.wrackTick(time, island.level, enchantments = island)
            }
        },
        IDLE(20L * 5) {
            override suspend fun tick(island: Island, time: Long) {
                island.wrackTick(time, island.level, enchantments = island)
            }
        };

        abstract suspend fun tick(island: Island, time: Long)
    }
}
