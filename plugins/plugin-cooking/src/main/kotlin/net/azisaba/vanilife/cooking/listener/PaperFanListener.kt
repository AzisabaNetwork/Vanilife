package net.azisaba.vanilife.cooking.listener

import net.azisaba.vanilife.cooking.CookingItems
import net.azisaba.vanilife.world.IslandsWorld
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector

internal class PaperFanListener : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action == Action.PHYSICAL) return
        val itemStack = event.item ?: return
        if (!itemStack.isOf(CookingItems.PAPER_FAN)) return

        val player = event.player
        if (player.hasCooldown(itemStack)) return
        player.setCooldown(itemStack, COOLDOWN_TICKS)

        event.hand?.let(player::swingHand)

        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction.normalize()
        val world = player.world

        world.playSound(eyeLocation, Sound.ENTITY_BREEZE_SHOOT, 0.6f, 1.5f)

        for (step in 1..4) {
            val point = eyeLocation.clone().add(direction.clone().multiply(step * 1.1))
            world.spawnParticle(
                Particle.CLOUD,
                point,
                5,
                0.25,
                0.2,
                0.25,
                0.04,
            )
        }

        val center = eyeLocation.clone().add(direction.clone().multiply(2.5))
        for (entity in world.getNearbyEntities(center, RANGE, RANGE, RANGE)) {
            if (entity == player || entity is ArmorStand) continue
            if (world is IslandsWorld && entity !is Item && entity !is Monster) continue
            if (entity !is Item && entity !is LivingEntity) continue
            if (entity is Player && entity.gameMode.isInvulnerable) continue

            val toEntity = entity.location.add(0.0, 0.5, 0.0).toVector().subtract(eyeLocation.toVector())
            val distance = toEntity.length()
            if (distance <= 0.001 || distance > RANGE) continue
            if (toEntity.clone().normalize().dot(direction) < 0.25) continue

            val strength = (1.0 - (distance / (RANGE + 1.0))).coerceIn(0.25, 1.0) * KNOCKBACK_STRENGTH
            val push = Vector(direction.x * strength, 0.18 * strength, direction.z * strength)
            entity.velocity = entity.velocity.add(push)
        }
    }

    private companion object {
        const val COOLDOWN_TICKS: Int = 10
        const val RANGE: Double = 4.5
        const val KNOCKBACK_STRENGTH: Double = 0.85
    }
}
