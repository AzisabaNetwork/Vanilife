package net.azisaba.vanilife.npc.listener

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import io.papermc.paper.entity.TeleportFlag
import net.azisaba.vanilife.npc.NpcMode
import net.azisaba.vanilife.npc.wrapper.NpcWrapper
import net.azisaba.vanilife.npc.wrapper.NpcWrapperMap
import org.bukkit.entity.Chicken
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityEnterLoveModeEvent
import org.bukkit.event.entity.EntityRemoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.event.world.EntitiesUnloadEvent
import org.bukkit.plugin.Plugin

internal class NpcDelegateListener(
    private val map: NpcWrapperMap,
    private val plugin: Plugin,
) : Listener {
    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val chicken = event.entity as? Chicken ?: return
        if (map.isDelegate(chicken)) {
            event.drops.clear()
            event.droppedExp = 0
        }
    }

    @EventHandler
    fun onEntityRemove(event: EntityRemoveEvent) {
        val delegate = event.entity as? Chicken ?: return
        val npcWrapper = map.byDelegate(delegate) ?: return
        map.unregister(npcWrapper)
        npcWrapper.dispose()
    }

    @EventHandler
    fun onEntityAddToWorld(event: EntityAddToWorldEvent) {
        val chicken = event.entity as? Chicken ?: return
        if (map.isDelegate(chicken)) return
        NpcWrapper.tryWrap(chicken)?.apply(map::register)
    }

    @EventHandler
    fun onEntitiesLoad(event: EntitiesLoadEvent) {
        for (chicken in event.entities.filterIsInstance<Chicken>()) {
            if (map.isDelegate(chicken)) continue
            NpcWrapper.tryWrap(chicken)?.apply(map::register)
        }
    }

    @EventHandler
    fun onEntitiesUnload(event: EntitiesUnloadEvent) {
        event.entities.filterIsInstance<Chicken>()
            .filter { it.persistentDataContainer.has(NpcWrapper.NPC_TYPE_KEY) }
            .mapNotNull(map::byDelegate)
            .forEach { npcWrapper ->
                map.unregister(npcWrapper)
                npcWrapper.dispose()
            }
    }

    @EventHandler
    fun onEntityEnterLoveMode(event: EntityEnterLoveModeEvent) {
        val chicken = event.entity as? Chicken ?: return
        if (map.isDelegate(chicken)) event.isCancelled = true
    }

    @EventHandler
    fun onEntityBreed(event: EntityBreedEvent) {
        val mother = event.mother as? Chicken
        if (mother != null && map.isDelegate(mother)) {
            event.isCancelled = true
            return
        }
        val father = event.father as? Chicken
        if (father != null && map.isDelegate(father)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val from = event.from
        val to = event.to
        if (from.world == to.world && from.distanceSquared(to) < TELEPORT_FOLLOW_DISTANCE_SQUARED) {
            return
        }

        val destination = to.clone()
        val followingNpcs = map.byOwner(event.player.uniqueId)
            .filter { !it.isDisposed && it.mode == NpcMode.FOLLOW }

        for (npcWrapper in followingNpcs) {
            npcWrapper.delegate.scheduler.run(
                plugin,
                {
                    if (npcWrapper.isDisposed || npcWrapper.mode != NpcMode.FOLLOW) return@run
                    npcWrapper.delegate.pathfinder.stopPathfinding()
                    npcWrapper.delegate.teleportAsync(
                        destination,
                        PlayerTeleportEvent.TeleportCause.PLUGIN,
                        TeleportFlag.Relative.VELOCITY_X,
                        TeleportFlag.Relative.VELOCITY_Y,
                        TeleportFlag.Relative.VELOCITY_Z,
                    )
                },
                null,
            )
        }
    }

    private companion object {
        const val TELEPORT_FOLLOW_DISTANCE_SQUARED: Double = 144.0
    }
}
