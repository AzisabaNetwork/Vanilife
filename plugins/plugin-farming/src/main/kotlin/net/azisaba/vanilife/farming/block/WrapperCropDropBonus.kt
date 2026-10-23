package net.azisaba.vanilife.farming.block

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import me.tofaa.entitylib.meta.MetaFields
import me.tofaa.entitylib.meta.enums.BillboardConstraints
import me.tofaa.entitylib.wrapper.WrapperEntity
import net.azisaba.vanilife.farming.FarmingTranslations
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

internal class WrapperCropDropBonus : WrapperEntity(EntityTypes.TEXT_DISPLAY) {
    init {
        consumeMetadata { meta ->
            meta.set(
                MetaFields.TextDisplay.TEXT,
                Component.translatable(FarmingTranslations.CROP_DROP_BONUS, NamedTextColor.GREEN),
            )
            meta.set(MetaFields.Display.BILLBOARD_RENDER_CONSTRAINTS, BillboardConstraints.CENTER)
        }
    }
}
