package net.azisaba.vanilife.island.leveling

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Vector3f
import me.tofaa.entitylib.meta.MetaFields
import me.tofaa.entitylib.meta.Metadata
import me.tofaa.entitylib.meta.enums.BillboardConstraints
import me.tofaa.entitylib.wrapper.WrapperEntity
import net.azisaba.vanilife.island.IslandFonts
import net.azisaba.vanilife.island.IslandTranslations
import net.kyori.adventure.text.Component
import kotlin.math.PI
import kotlin.math.sin

internal class WrapperLevelUpEffect(private val level: Int) : WrapperEntity(EntityTypes.TEXT_DISPLAY) {
    init {
        consumeMetadata { meta ->
            meta.set(
                MetaFields.TextDisplay.TEXT,
                Component.text()
                    .append(Component.text(IslandFonts.LevelIcons.levelUpOf(level)).font(IslandFonts.LEVEL_ICONS))
                    .appendSpace()
                    .append(Component.translatable(IslandTranslations.ISLAND_LEVEL_UP))
                    .build(),
            )
            meta.set(MetaFields.Display.BILLBOARD_RENDER_CONSTRAINTS, BillboardConstraints.CENTER)
            meta.set(MetaFields.TextDisplay.BACKGROUND_COLOR, 0)
            meta.applyAnimation(0L)
        }
    }

    fun tick(time: Long) {
        consumeMetadata { meta ->
            meta.set(MetaFields.Display.TRANSFORMATION_INTERPOLATION_DURATION, 4)
            meta.applyAnimation(time)
        }
    }

    private fun Metadata.applyAnimation(time: Long) {
        val progress = (time % BOUNCE_PERIOD_TICKS).toFloat() / BOUNCE_PERIOD_TICKS
        val bounce = sin(progress * PI).toFloat()

        val bounceScale = BASE_SCALE + (bounce * SCALE_AMPLITUDE)
        val y = BASE_TRANSLATION_Y + (bounce * TRANSLATION_AMPLITUDE)

        set(MetaFields.Display.SCALE, Vector3f(bounceScale, bounceScale, bounceScale))
        set(MetaFields.Display.TRANSLATION, Vector3f(0f, y, 0f))
    }

    private companion object {
        const val BASE_SCALE: Float = 0.6f
        const val SCALE_AMPLITUDE: Float = 0.18f
        const val BASE_TRANSLATION_Y: Float = 0.15f
        const val TRANSLATION_AMPLITUDE: Float = 0.45f
        const val BOUNCE_PERIOD_TICKS: Int = 16
    }
}
