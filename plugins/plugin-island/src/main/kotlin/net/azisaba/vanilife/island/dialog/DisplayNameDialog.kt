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
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DisplayNameDialog : KoinComponent {
    val TITLE: Component = Component.text()
        .append(
            Component.text(IslandFonts.IslandIcons.DISPLAY_NAME)
                .shadowColor(ShadowColor.none())
                .font(IslandFonts.ISLAND_ICONS)
        )
        .appendSpace()
        .append(Component.translatable(IslandTranslations.DIALOG_VANILIFE_DISPLAY_NAME))
        .build()

    private val plugin: Plugin by inject()

    // Restrict player input to styling tags so names cannot carry click/hover/insertion events
    private val nameMiniMessage: MiniMessage = MiniMessage.builder()
        .tags(
            TagResolver.resolver(
                StandardTags.color(),
                StandardTags.decorations(),
                StandardTags.gradient(),
                StandardTags.rainbow(),
                StandardTags.reset(),
            )
        )
        .build()

    fun create(island: Island): Dialog = Dialog.create { builder ->
        builder.empty()
            .base(
                DialogBase.builder(TITLE)
                    .inputs(
                        listOf(
                            DialogInput.text(
                                "new_name",
                                Component.translatable(IslandTranslations.DIALOG_VANILIFE_DISPLAY_NAME_NEW_NAME)
                            ).build(),
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
                                    val newName = response.getText("new_name") ?: return@customClick
                                    plugin.launch {
                                        island.displayName(nameMiniMessage.deserialize(newName))
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
