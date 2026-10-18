package net.azisaba.vanilife.island.dialog

import com.github.shynixn.mccoroutine.folia.launch
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.azisaba.vanilife.island.Island
import net.azisaba.vanilife.island.IslandFonts
import net.azisaba.vanilife.island.IslandTranslations
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.format.ShadowColor
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SkyColorDialog : KoinComponent {
    val TITLE: Component = Component.text()
        .append(
            Component.text(IslandFonts.IslandIcons.SKY_COLOR)
                .shadowColor(ShadowColor.none())
                .font(IslandFonts.ISLAND_ICONS)
        )
        .appendSpace()
        .append(Component.translatable(IslandTranslations.DIALOG_VANILIFE_SKY_COLOR))
        .build()

    private val plugin: Plugin by inject()

    fun create(island: Island): Dialog = Dialog.create { builder ->
        builder.empty()
            .base(
                DialogBase.builder(TITLE)
                    .inputs(
                        listOf(
                            DialogInput.numberRange(
                                "rain_level",
                                Component.translatable(IslandTranslations.DIALOG_VANILIFE_SKY_COLOR_RAIN_LEVEL),
                                0f,
                                10f,
                            )
                                .initial(island.skyRainLevel)
                                .step(0.1f)
                                .build(),
                            DialogInput.numberRange(
                                "thunder_level",
                                Component.translatable(IslandTranslations.DIALOG_VANILIFE_SKY_COLOR_THUNDER_LEVEL),
                                0f,
                                10f,
                            )
                                .initial(island.skyThunderLevel)
                                .step(0.1f)
                                .build(),
                        )
                    )
                    .build()
            )
            .type(
                DialogType.notice(
                    ActionButton.builder(Component.translatable("gui.ok"))
                        .action(
                            DialogAction.customClick(
                                { response, _ ->
                                    val rainLevel = response.getFloat("rain_level") ?: island.skyRainLevel
                                    val thunderLevel = response.getFloat("thunder_level") ?: island.skyThunderLevel
                                    plugin.launch {
                                        island.skyColor(rainLevel, thunderLevel)
                                    }
                                },
                                ClickCallback.Options.builder()
                                    .uses(ClickCallback.UNLIMITED_USES)
                                    .build()
                            )
                        )
                        .build()
                )
            )
    }
}
