package net.azisaba.vanilife.island.wrack

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Quaternion4f
import com.github.retrooper.packetevents.util.Vector3f
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import me.tofaa.entitylib.meta.MetaFields
import me.tofaa.entitylib.meta.enums.BillboardConstraints
import me.tofaa.entitylib.meta.enums.ItemDisplayContext
import me.tofaa.entitylib.wrapper.WrapperEntity
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal class WrapperWrackEnchantmentDisplay(
    private val origin: org.bukkit.Location,
) : WrapperEntity(EntityTypes.ITEM_DISPLAY) {
    init {
        consumeMetadata { meta ->
            meta.set(MetaFields.ItemDisplay.ITEM_STACK, SpigotConversionUtil.fromBukkitItemStack(ItemStack(Material.ENCHANTED_BOOK)))
            meta.set(MetaFields.ItemDisplay.ITEM_DISPLAY, ItemDisplayContext.GROUND)
            meta.set(MetaFields.Display.BILLBOARD_RENDER_CONSTRAINTS, BillboardConstraints.FIXED)
            meta.set(MetaFields.Display.TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS, 0)
            meta.set(MetaFields.Display.TRANSFORMATION_INTERPOLATION_DURATION, 0)
            meta.set(MetaFields.Display.SCALE, Vector3f(0.2f, 0.2f, 0.2f))
            meta.set(MetaFields.Display.TRANSLATION, Vector3f(0f, 0f, 0f))
            meta.set(MetaFields.Display.RIGHT_ROTATION, axisAngle(1f, 0f, 0f, 20f))
            meta.setFlag(MetaFields.Entity.GLOWING, true)
            meta.set(MetaFields.Display.GLOW_COLOR_OVERRIDE, 0x7A5BC2)
        }
    }

    fun tick(animationTick: Int, totalTicks: Int) {
        val progress = (animationTick.toFloat() / totalTicks.toFloat()).coerceIn(0f, 1f)
        val eased = 1f - (1f - progress) * (1f - progress)
        val rotation = (PI.toFloat() * 3f) * progress
        val scale = 0.2f + eased * 0.95f
        val verticalOffset = eased * 0.45f

        consumeMetadata { meta ->
            meta.set(MetaFields.Display.TRANSFORMATION_INTERPOLATION_DURATION, 1)
            meta.set(MetaFields.Display.SCALE, Vector3f(scale, scale, scale))
            meta.set(MetaFields.Display.TRANSLATION, Vector3f(0f, verticalOffset, 0f))
            meta.set(MetaFields.Display.LEFT_ROTATION, yawRotation(rotation))
        }

        origin.world.spawnParticle(
            Particle.ENCHANT,
            origin.x,
            origin.y + 0.2 + verticalOffset,
            origin.z,
            6,
            0.15 + eased * 0.1,
            0.18,
            0.15 + eased * 0.1,
            0.02,
        )
    }

    fun finish() {
        origin.world.spawnParticle(
            Particle.FIREWORK,
            origin.x,
            origin.y + 0.7,
            origin.z,
            24,
            0.22,
            0.25,
            0.22,
            0.02,
        )
        remove()
    }

    private fun yawRotation(radians: Float): Quaternion4f {
        val half = radians / 2f
        return Quaternion4f(0f, sin(half), 0f, cos(half))
    }

    private fun axisAngle(ax: Float, ay: Float, az: Float, degrees: Float): Quaternion4f {
        val len = sqrt((ax * ax + ay * ay + az * az).toDouble()).toFloat()
        val nx = ax / len
        val ny = ay / len
        val nz = az / len
        val half = Math.toRadians((degrees / 2f).toDouble())
        val s = sin(half).toFloat()
        val c = cos(half).toFloat()
        return Quaternion4f(nx * s, ny * s, nz * s, c)
    }
}
