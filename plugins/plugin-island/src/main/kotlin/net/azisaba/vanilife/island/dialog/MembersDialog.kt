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
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.ShadowColor
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object MembersDialog : KoinComponent {
    val TITLE: Component = Component.text()
        .append(
            Component.text(IslandFonts.IslandIcons.MEMBERS, NamedTextColor.WHITE)
                .shadowColor(ShadowColor.none())
                .font(IslandFonts.ISLAND_ICONS)
        )
        .appendSpace()
        .append(Component.translatable(IslandTranslations.DIALOG_VANILIFE_MEMBERS))
        .build()

    private val plugin: Plugin by inject()

    fun create(island: Island): Dialog = Dialog.create { builder ->
        val buttons = buildList {
            add(addMemberButton(island))
            island.members.forEach { memberUuid ->
                add(removeMemberButton(island, memberUuid))
            }
        }

        builder.empty()
            .base(
                DialogBase.builder(TITLE)
                    .inputs(
                        listOf(
                            DialogInput.text(
                                "player_name",
                                Component.translatable(IslandTranslations.DIALOG_VANILIFE_MEMBERS_PLAYER_NAME),
                            )
                                .maxLength(16)
                                .build()
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

    private fun addMemberButton(island: Island): ActionButton = ActionButton.builder(
        Component.translatable(IslandTranslations.DIALOG_VANILIFE_MEMBERS_ADD, NamedTextColor.GREEN)
    )
        .action(
            DialogAction.customClick(
                { response, audience ->
                    val name = response.getText("player_name")?.trim().orEmpty()
                    if (name.isEmpty()) return@customClick
                    val target = Bukkit.getOfflinePlayerIfCached(name) ?: Bukkit.getPlayerExact(name)
                    if (target == null || target.uniqueId == island.owner) return@customClick
                    plugin.launch {
                        island.unbanVisitor(target.uniqueId)
                        island.addMember(target.uniqueId)
                        audience.showDialog(create(island))
                    }
                },
                ClickCallback.Options.builder()
                    .uses(ClickCallback.UNLIMITED_USES)
                    .build()
            )
        )
        .build()

    private fun removeMemberButton(island: Island, memberUuid: UUID): ActionButton {
        val memberName = Bukkit.getOfflinePlayer(memberUuid).name ?: memberUuid.toString().take(8)
        return ActionButton.builder(
            Component.translatable(
                IslandTranslations.DIALOG_VANILIFE_MEMBERS_REMOVE,
                NamedTextColor.RED,
                Component.text(memberName, NamedTextColor.WHITE),
            )
        )
            .action(
                DialogAction.customClick(
                    { _, audience ->
                        plugin.launch {
                            island.removeMember(memberUuid)
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
