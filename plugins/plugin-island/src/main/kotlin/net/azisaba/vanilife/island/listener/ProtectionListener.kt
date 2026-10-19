package net.azisaba.vanilife.island.listener

import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent
import net.azisaba.vanilife.island.IslandPlayerMap
import net.azisaba.vanilife.island.cache.IslandCacheMap
import net.azisaba.vanilife.world.IslandPosition
import net.azisaba.vanilife.world.IslandsWorld
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.Directional
import org.bukkit.entity.Enderman
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFertilizeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketEntityEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTakeLecternBookEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerUnleashEntityEvent
import org.bukkit.event.vehicle.VehicleDamageEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.event.world.StructureGrowEvent
import org.bukkit.util.Vector

internal class ProtectionListener(private val cacheMap: IslandCacheMap) : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!canModify(event.player, event.block.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!canModify(event.player, event.block.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockMultiPlace(event: BlockMultiPlaceEvent) {
        if (event.replacedBlockStates.any { !canModify(event.player, it.location) }) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockFertilize(event: BlockFertilizeEvent) {
        val player = event.player ?: return
        if (!canModify(player, event.block.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockIgnite(event: BlockIgniteEvent) {
        val player = event.player
        if (player != null) {
            if (!canModify(player, event.block.location)) {
                event.isCancelled = true
            }
            return
        }
        val ignitingEntity = event.ignitingEntity
        if (ignitingEntity != null && event.block.world is IslandsWorld) {
            val attacker = resolveAttacker(ignitingEntity)
            if (attacker == null || !canModify(attacker, event.block.location)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockExplode(event: BlockExplodeEvent) {
        if (event.block.world is IslandsWorld) {
            event.blockList().clear()
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (event.location.world is IslandsWorld) {
            event.blockList().clear()
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockPistonExtend(event: BlockPistonExtendEvent) {
        if (event.block.world !is IslandsWorld) return
        val originPosition = IslandPosition.fromPosition(event.block.location)
        val destinationAfterPiston = event.block.getRelative(event.direction).location
        if (!originPosition.isWithinBoundary(destinationAfterPiston) ||
            IslandPosition.fromPosition(destinationAfterPiston) != originPosition
        ) {
            event.isCancelled = true
            return
        }
        val outOfBounds = event.blocks.any { block ->
            val dest = block.getRelative(event.direction).location
            !originPosition.isWithinBoundary(block.location) ||
                !originPosition.isWithinBoundary(dest) ||
                IslandPosition.fromPosition(dest) != originPosition
        }
        if (outOfBounds) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockPistonRetract(event: BlockPistonRetractEvent) {
        if (event.block.world !is IslandsWorld) return
        val originPosition = IslandPosition.fromPosition(event.block.location)
        val outOfBounds = event.blocks.any { block ->
            val dest = block.getRelative(event.direction).location
            !originPosition.isWithinBoundary(block.location) ||
                !originPosition.isWithinBoundary(dest) ||
                IslandPosition.fromPosition(block.location) != originPosition
        }
        if (outOfBounds) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockFromTo(event: BlockFromToEvent) {
        if (event.block.world !is IslandsWorld) return
        val originPosition = IslandPosition.fromPosition(event.block.location)
        val toLocation = event.toBlock.location
        if (!originPosition.isWithinBoundary(toLocation) ||
            IslandPosition.fromPosition(toLocation) != originPosition
        ) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockDispense(event: BlockDispenseEvent) {
        if (event.block.world !is IslandsWorld) return
        val directional = event.block.blockData as? Directional ?: return
        val originPosition = IslandPosition.fromPosition(event.block.location)
        val targetLocation = event.block.getRelative(directional.facing).location
        if (!originPosition.isWithinBoundary(targetLocation) ||
            IslandPosition.fromPosition(targetLocation) != originPosition
        ) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onStructureGrow(event: StructureGrowEvent) {
        if (event.world !is IslandsWorld) return
        val player = event.player
        if (player != null && !canModify(player, event.location)) {
            event.isCancelled = true
            return
        }
        val originPosition = IslandPosition.fromPosition(event.location)
        event.blocks.removeIf { state ->
            !originPosition.isWithinBoundary(state.location) ||
                IslandPosition.fromPosition(state.location) != originPosition
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        if (!canModify(event.player, event.block.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerBucketFill(event: PlayerBucketFillEvent) {
        if (!canModify(event.player, event.block.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerBucketEntity(event: PlayerBucketEntityEvent) {
        if (!canModify(event.player, event.entity.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock ?: return
        if (event.action.isRightClick && !event.player.isSneaking &&
            (clickedBlock.type == Material.ENCHANTING_TABLE || clickedBlock.type == Material.CRAFTING_TABLE)
        ) {
            return
        }
        if (!canModify(event.player, clickedBlock.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (!canModify(event.player, event.rightClicked.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        if (!canModify(event.player, event.rightClicked.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerArmorStandManipulate(event: PlayerArmorStandManipulateEvent) {
        if (!canModify(event.player, event.rightClicked.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerFlowerPotManipulate(event: PlayerFlowerPotManipulateEvent) {
        if (!canModify(event.player, event.flowerpot.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerTakeLecternBook(event: PlayerTakeLecternBookEvent) {
        if (!canModify(event.player, event.lectern.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerBedEnter(event: PlayerBedEnterEvent) {
        if (!canModify(event.player, event.bed.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerLeashEntity(event: PlayerLeashEntityEvent) {
        if (!canModify(event.player, event.entity.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerUnleashEntity(event: PlayerUnleashEntityEvent) {
        if (!canModify(event.player, event.entity.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerHarvestBlock(event: PlayerHarvestBlockEvent) {
        if (!canModify(event.player, event.harvestedBlock.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onHangingBreak(event: HangingBreakEvent) {
        if (event.entity.world !is IslandsWorld) return
        if (event.cause == HangingBreakEvent.RemoveCause.EXPLOSION) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onHangingBreakByEntity(event: HangingBreakByEntityEvent) {
        if (event.entity.world !is IslandsWorld) return
        val attacker = resolveAttacker(event.remover)
        if (attacker == null || !canModify(attacker, event.entity.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onHangingPlace(event: HangingPlaceEvent) {
        val player = event.player ?: return
        if (!canModify(player, event.entity.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        if (event.block.world !is IslandsWorld) return
        if (event.entity is Enderman) {
            event.isCancelled = true
            return
        }
        val attacker = resolveAttacker(event.entity) ?: return
        if (!canModify(attacker, event.block.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onProjectileHit(event: ProjectileHitEvent) {
        val hitBlock = event.hitBlock ?: return
        if (hitBlock.world !is IslandsWorld) return
        val attacker = resolveAttacker(event.entity) ?: return
        if (!canModify(attacker, hitBlock.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity.world !is IslandsWorld) return
        val attacker = resolveAttacker(event.damager) ?: return
        if (event.entity is Player || !canModify(attacker, event.entity.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onVehicleDamage(event: VehicleDamageEvent) {
        if (event.vehicle.world !is IslandsWorld) return
        val attacker = resolveAttacker(event.attacker) ?: return
        if (!canModify(attacker, event.vehicle.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        if (event.vehicle.world !is IslandsWorld) return
        val attacker = resolveAttacker(event.attacker) ?: return
        if (!canModify(attacker, event.vehicle.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        val to = event.to
        if (event.from.blockX == to.blockX && event.from.blockZ == to.blockZ) return
        if (to.world !is IslandsWorld) return
        val player = event.player
        if (player.isOp && player.gameMode.isInvulnerable) return

        val allowedPosition = IslandPlayerMap.lookup(player.uniqueId)?.position
            ?: IslandPosition.fromPosition(event.from)
        if (!allowedPosition.isWithinBoundary(to)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (event.cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL &&
            event.cause != PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT
        ) {
            return
        }
        val to = event.to
        if (to.world !is IslandsWorld) return
        val player = event.player
        if (player.isOp && player.gameMode.isInvulnerable) return

        val allowedPosition = IslandPlayerMap.lookup(player.uniqueId)?.position
            ?: IslandPosition.fromPosition(event.from)
        if (!allowedPosition.isWithinBoundary(to) || !canModify(player, to)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onVehicleMove(event: VehicleMoveEvent) {
        val to = event.to
        if (event.from.blockX == to.blockX && event.from.blockZ == to.blockZ) return
        if (to.world !is IslandsWorld) return

        val passengers = event.vehicle.passengers.filterIsInstance<Player>()
        if (passengers.isEmpty()) return

        val outOfBounds = passengers.any { player ->
            if (player.isOp && player.gameMode.isInvulnerable) return@any false
            val allowedPosition = IslandPlayerMap.lookup(player.uniqueId)?.position
                ?: IslandPosition.fromPosition(event.from)
            !allowedPosition.isWithinBoundary(to)
        }
        if (outOfBounds) {
            event.vehicle.velocity = Vector(0, 0, 0)
            event.vehicle.teleportAsync(event.from)
        }
    }

    private fun canModify(player: Player, location: Location): Boolean {
        if (location.world !is IslandsWorld) return true
        if (player.isOp && player.gameMode.isInvulnerable) return true

        val targetPosition = IslandPosition.fromPosition(location)
        if (!targetPosition.isWithinBoundary(location)) return false

        val ownedPosition = cacheMap.lookupCachedPosition(player.uniqueId) ?: return false
        return targetPosition == ownedPosition
    }

    private fun resolveAttacker(entity: Entity?): Player? = when (entity) {
        is Player -> entity
        is Projectile -> entity.shooter as? Player
        else -> null
    }
}
