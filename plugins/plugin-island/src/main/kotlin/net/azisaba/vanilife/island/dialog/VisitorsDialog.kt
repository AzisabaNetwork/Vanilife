package net.azisaba.vanilife.island.dialog

import com.github.shynixn.mccoroutine.folia.launch
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.azisaba.vanilife.island.Island
import net.azisaba.vanilife.island.IslandFonts
import net.azisaba.vanilife.island.IslandPlayerMap
import net.azisaba.vanilife.island.IslandTranslations
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.ShadowColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object VisitorsDialog : KoinComponent {
    val TITLE: Component = Component.text()
        .append(
            Component.text(IslandFonts.IslandIcons.VISITORS, NamedTextColor.WHITE)
                .shadowColor(ShadowColor.none())
                .font(IslandFonts.ISLAND_ICONS)
        )
        .appendSpace()
        .append(Component.translatable(IslandTranslations.DIALOG_VANILIFE_VISITORS))
        .build()

    private val plugin: Plugin by inject()

    fun create(island: Island): Dialog = Dialog.create { builder ->
        val onlineVisitors = IslandPlayerMap.collect(island)
            .filter { it != island.owner }
            .mapNotNull(Bukkit::getPlayer)

        val buttons = buildList {
            add(saveAndBanInputButton(island))
            onlineVisitors.forEach { visitor ->
                add(kickButton(island, visitor))
                if (!island.isBanned(visitor.uniqueId)) {
                    add(banOnlineButton(island, visitor))
                }
            }
            island.bannedPlayers.forEach { bannedUuid ->
                add(unbanButton(island, bannedUuid))
            }
        }

        builder.empty()
            .base(
                DialogBase.builder(TITLE)
                    .inputs(
                        listOf(
                            DialogInput.singleOption(
                                "access",
                                Component.translatable(IslandTranslations.DIALOG_VANILIFE_VISITORS_ACCESS),
                                listOf(
                                    SingleOptionDialogInput.OptionEntry.create(
                                        "public",
                                        Component.translatable(IslandTranslations.DIALOG_VANILIFE_VISITORS_PUBLIC),
                                        island.isVisitable,
                                    ),
                                    SingleOptionDialogInput.OptionEntry.create(
                                        "private",
                                        Component.translatable(IslandTranslations.DIALOG_VANILIFE_VISITORS_PRIVATE),
                                        !island.isVisitable,
                                    ),
                                )
                            ).build(),
                            DialogInput.text(
                                "ban_player_name",
                                Component.translatable(IslandTranslations.DIALOG_VANILIFE_VISITORS_PLAYER_NAME),
                            )
                                .maxLength(16)
                                .build(),
                        )
                    )
                    .build()
            )
            .type(
                DialogType.multiAction(buttons)
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

    private fun saveAndBanInputButton(island: Island): ActionButton = ActionButton.builder(
        Component.translatable(IslandTranslations.DIALOG_VANILIFE_VISITORS_BAN_INPUT, NamedTextColor.GREEN)
    )
        .action(
            DialogAction.customClick(
                { response, audience ->
                    val accessValue = response.getText("access")
                    val visitable = accessValue != "private"
                    val banName = response.getText("ban_player_name")?.trim().orEmpty()
                    val targetToBan = if (banName.isNotEmpty()) {
                        Bukkit.getOfflinePlayerIfCached(banName) ?: Bukkit.getPlayerExact(banName)
                    } else {
                        null
                    }
                    plugin.launch {
                        if (island.isVisitable != visitable) {
                            island.visitable(visitable)
                        }
                        if (targetToBan != null && targetToBan.uniqueId != island.owner) {
                            island.banVisitor(targetToBan.uniqueId)
                        }
                        audience.showDialog(create(island))
                    }
                },
                ClickCallback.Options.builder()
                    .uses(ClickCallback.UNLIMITED_USES)
                    .build()
            )
        )
        .build()

    private fun kickButton(island: Island, visitor: Player): ActionButton = ActionButton.builder(
        Component.translatable(
            IslandTranslations.DIALOG_VANILIFE_VISITORS_KICK,
            NamedTextColor.YELLOW,
            Component.text(visitor.name, NamedTextColor.WHITE),
        )
    )
        .action(
            DialogAction.customClick(
                { _, audience ->
                    plugin.launch {
                        island.kickVisitor(visitor)
                        audience.showDialog(create(island))
                    }
                },
                ClickCallback.Options.builder()
                    .uses(ClickCallback.UNLIMITED_USES)
                    .build()
            )
        )
        .build()

    private fun banOnlineButton(island: Island, visitor: Player): ActionButton = ActionButton.builder(
        Component.translatable(
            IslandTranslations.DIALOG_VANILIFE_VISITORS_BAN,
            NamedTextColor.RED,
            Component.text(visitor.name, NamedTextColor.WHITE),
        )
    )
        .action(
            DialogAction.customClick(
                { _, audience ->
                    plugin.launch {
                        island.banVisitor(visitor.uniqueId)
                        audience.showDialog(create(island))
                    }
                },
                ClickCallback.Options.builder()
                    .uses(ClickCallback.UNLIMITED_USES)
                    .build()
            )
        )
        .build()

    private fun unbanButton(island: Island, bannedUuid: UUID): ActionButton {
        val bannedName = Bukkit.getOfflinePlayer(bannedUuid).name ?: bannedUuid.toString().take(8)
        return ActionButton.builder(
            Component.translatable(
                IslandTranslations.DIALOG_VANILIFE_VISITORS_UNBAN,
                NamedTextColor.AQUA,
                Component.text(bannedName, NamedTextColor.WHITE),
            )
        )
            .action(
                DialogAction.customClick(
                    { _, audience ->
                        plugin.launch {
                            island.unbanVisitor(bannedUuid)
                            audience.showDialog(create(island))
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
