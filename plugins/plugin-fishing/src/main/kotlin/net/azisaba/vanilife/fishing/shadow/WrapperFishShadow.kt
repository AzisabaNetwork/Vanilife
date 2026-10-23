package net.azisaba.vanilife.fishing.shadow

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.world.Location
import com.github.retrooper.packetevents.util.Quaternion4f
import com.github.retrooper.packetevents.util.Vector3f
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.papermc.paper.math.FinePosition
import me.tofaa.entitylib.meta.MetaFields
import me.tofaa.entitylib.meta.enums.BillboardConstraints
import me.tofaa.entitylib.wrapper.WrapperEntity
import net.azisaba.vanilife.fishing.FishingFonts
import net.azisaba.vanilife.fishing.ai.FishBehavior
import net.kyori.adventure.text.Component
import org.bukkit.entity.FishHook
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class WrapperFishShadow(
    private val fishHook: FishHook,
    private val behavior: FishBehavior,
) : WrapperEntity(EntityTypes.TEXT_DISPLAY) {
    val translatedLocation: Location
        get() {
            val translation = metadata.get(MetaFields.Display.TRANSLATION) ?: Vector3f(0f, 0f, 0f)
            val translatedPosition = location.position.add(
                translation.x.toDouble(),
                translation.y.toDouble(),
                translation.z.toDouble()
            )
            return Location(translatedPosition, location.yaw, location.pitch)
        }

    var state: State = State.APPROACHING
        private set

    init {
        consumeMetadata { meta ->
            meta.set(
                MetaFields.TextDisplay.TEXT,
                Component.text(FishingFonts.FishShadows.FISH_SHADOW).font(FishingFonts.FISH_SHADOWS),
            )
            meta.set(MetaFields.Display.BILLBOARD_RENDER_CONSTRAINTS, BillboardConstraints.FIXED)
            meta.set(MetaFields.TextDisplay.BACKGROUND_COLOR, 0)
            meta.set(MetaFields.Display.BRIGHTNESS_OVERRIDE, 0x00f000f0)
            meta.set(MetaFields.Display.TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS, 0)
            meta.set(MetaFields.Display.TRANSFORMATION_INTERPOLATION_DURATION, 5)
            meta.set(MetaFields.Display.LEFT_ROTATION, createAxisAngleQuaternion(1f, 0f, 0f, -90f))
            meta.set(MetaFields.Display.RIGHT_ROTATION, Quaternion4f(0f, 0f, 0f, 1f))
            meta.set(MetaFields.Display.TRANSLATION, Vector3f(INITIAL_OFFSET_X, SURFACE_OFFSET_Y, 0f))
        }
    }

    fun tick(time: Long) {
        when (state) {
            State.APPROACHING -> approachTick(time)
            State.FIGHTING -> fightTick(time)
        }
    }

    private fun approachTick(time: Long) {
        val currentPosition = computeCurrentSurfacePosition()
        val fishHookSurfaceLocation = computeFishHookSurfaceLocation()
        val nextPosition = behavior.approach.tick(
            time,
            currentPosition,
            fishHookSurfaceLocation
        )

        if (nextPosition == null) {
            state = State.FIGHTING
            return
        }

        updateTranslationAndRotation(nextPosition)
    }

    private fun fightTick(time: Long) {
        val currentPosition = computeCurrentSurfacePosition()
        val fishHookSurfaceLocation = computeFishHookSurfaceLocation()
        updateTranslationAndRotation(
            behavior.fighting.tick(
                time,
                currentPosition,
                fishHookSurfaceLocation,
            )
        )
    }

    private fun updateTranslationAndRotation(position: FinePosition) {
        val currentPosition = computeCurrentSurfacePosition()
        val surfaceY = computeFishHookSurfaceLocation().y
        val translation = Vector3f(
            (position.x() - x).toFloat(),
            (surfaceY - y).toFloat(),
            (position.z() - z).toFloat()
        )

        consumeMetadata { meta ->
            meta.set(MetaFields.Display.TRANSLATION, translation)
            val directionX = (position.x() - currentPosition.x()).toFloat()
            val directionZ = (position.z() - currentPosition.z()).toFloat()
            if (directionX != 0f || directionZ != 0f) {
                val angle = atan2(-directionX, -directionZ)
                val half = angle / 2f
                meta.set(MetaFields.Display.RIGHT_ROTATION, Quaternion4f(0f, 0f, sin(half), cos(half)))
            }
        }
    }

    private fun computeCurrentSurfacePosition(): org.bukkit.Location = SpigotConversionUtil.toBukkitLocation(fishHook.world, translatedLocation).apply {
        y = computeFishHookSurfaceLocation().y
        yaw = 0f
        pitch = 0f
    }

    private fun computeFishHookSurfaceLocation(): org.bukkit.Location = fishHook.location.clone().apply {
        val surfaceBlockY = blockY
        val fluidHeight = world.getFluidData(this).computeHeight(this)
        y = surfaceBlockY + fluidHeight.toDouble()
        yaw = 0f
        pitch = 0f
    }

    private fun createAxisAngleQuaternion(ax: Float, ay: Float, az: Float, degrees: Float): Quaternion4f {
        val length = sqrt((ax * ax + ay * ay + az * az).toDouble()).toFloat()
        val normalizedX = ax / length
        val normalizedY = ay / length
        val normalizedZ = az / length
        val half = Math.toRadians((degrees / 2.0))
        val sin = sin(half).toFloat()
        val cos = cos(half).toFloat()
        return Quaternion4f(normalizedX * sin, normalizedY * sin, normalizedZ * sin, cos)
    }

    enum class State {
        APPROACHING,
        FIGHTING,
    }

    companion object {
        private const val INITIAL_OFFSET_X = 0.36f
        private const val SURFACE_OFFSET_Y = 0.02f
    }
}
