package net.azisaba.vanilife.island.dialog

import com.github.shynixn.mccoroutine.folia.launch
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.azisaba.vanilife.island.Island
import net.azisaba.vanilife.island.IslandFeature
import net.azisaba.vanilife.island.IslandFonts
import net.azisaba.vanilife.island.IslandPlayerMap
import net.azisaba.vanilife.island.IslandTranslations
import net.azisaba.vanilife.island.cache.IslandCacheMap
import net.azisaba.vanilife.island.ownedIsland
import net.azisaba.vanilife.island.teleport
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.ShadowColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TravelDialog : KoinComponent {
    val TITLE: Component = Component.text()
        .append(
            Component.text(IslandFonts.IslandIcons.TRAVEL, NamedTextColor.WHITE)
                .shadowColor(ShadowColor.none())
                .font(IslandFonts.ISLAND_ICONS)
        )
        .appendSpace()
        .append(Component.translatable(IslandTranslations.ISLAND_FEATURE_VISIT_OTHER_ISLANDS))
        .build()

    private val cacheMap: IslandCacheMap by inject()
    private val plugin: Plugin by inject()

    suspend fun create(player: Player): Dialog {
        val ownIsland = player.ownedIsland()
        if (!ownIsland.isEnabled(IslandFeature.VISIT_OTHER_ISLANDS)) {
            return createLockedDialog()
        }

        val candidates = cacheMap.listIslands()
            .filter { it.position != ownIsland.position }
            .sortedWith(
                compareByDescending<Island> { IslandPlayerMap.collect(it).size }
                    .thenByDescending(Island::level)
                    .thenByDescending(Island::score)
            )

        if (candidates.isEmpty()) {
            return createEmptyDialog()
        }

        return Dialog.create { builder ->
            builder.empty()
                .base(
                    DialogBase.builder(TITLE)
                        .body(
                            listOf(
                                DialogBody.plainMessage(
                                    Component.translatable(IslandTranslations.DIALOG_VANILIFE_TRAVEL_DESCRIPTION)
                                )
                            )
                        )
                        .build()
                )
                .type(
                    DialogType.multiAction(candidates.map(::islandButton))
                        .exitAction(
                            ActionButton.builder(Component.translatable("gui.done"))
                                .action(
                                    DialogAction.customClick(
                                        { _, audience -> audience.closeDialog() },
                                        ClickCallback.Options.builder()
                                            .uses(ClickCallback.UNLIMITED_USES)
                                            .build()
                                    )
                                )
                                .build()
                        )
                        .columns(1)
                        .build()
                )
        }
    }

    private fun createLockedDialog(): Dialog {
        val requiredLevel = IslandFeature.VISIT_OTHER_ISLANDS.requiredLevel
        val levelComponent = Component.text()
            .color(NamedTextColor.WHITE)
            .append(
                Component.text(IslandFonts.LevelIcons.levelOf(requiredLevel))
                    .font(IslandFonts.LEVEL_ICONS)
                    .shadowColor(ShadowColor.none())
            )
            .append(Component.text(requiredLevel))
            .build()

        return Dialog.create { builder ->
            builder.empty()
                .base(
                    DialogBase.builder(TITLE)
                        .body(
                            listOf(
                                DialogBody.plainMessage(
                                    Component.translatable(
                                        IslandTranslations.DIALOG_VANILIFE_TRAVEL_LOCKED,
                                        NamedTextColor.GRAY,
                                        levelComponent,
                                    )
                                )
                            )
                        )
                        .build()
                )
                .type(DialogType.notice())
        }
    }

    private fun createEmptyDialog(): Dialog = Dialog.create { builder ->
        builder.empty()
            .base(
                DialogBase.builder(TITLE)
                    .body(
                        listOf(
                            DialogBody.plainMessage(
                                Component.translatable(
                                    IslandTranslations.DIALOG_VANILIFE_TRAVEL_EMPTY,
                                    NamedTextColor.GRAY,
                                )
                            )
                        )
                    )
                    .build()
            )
            .type(DialogType.notice())
    }

    private fun islandButton(island: Island): ActionButton {
        val ownerName = Bukkit.getOfflinePlayer(island.owner).name ?: island.owner.toString().take(8)
        val label = Component.text()
            .append(
                Component.text(IslandFonts.LevelIcons.levelOf(island.level), NamedTextColor.WHITE)
                    .font(IslandFonts.LEVEL_ICONS)
                    .shadowColor(ShadowColor.none())
            )
            .append(Component.text(island.level, NamedTextColor.WHITE))
            .appendSpace()
            .append(island.displayName)
            .appendSpace()
            .append(Component.text("($ownerName)", NamedTextColor.GRAY))
            .build()

        return ActionButton.builder(label)
            .action(
                DialogAction.customClick(
                    { _, audience ->
                        val visitor = audience as? Player ?: return@customClick
                        visitor.closeDialog()
                        plugin.launch {
                            visitor.teleport(island)
                        }
                    },
                    ClickCallback.Options.builder()
                        .uses(ClickCallback.UNLIMITED_USES)
                        .build()
                )
            )
            .build()
    }
}
