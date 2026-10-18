package net.azisaba.vanilife.npc.dialog

import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.azisaba.vanilife.npc.NpcMode
import net.azisaba.vanilife.npc.NpcTranslations
import net.azisaba.vanilife.npc.wrapper.NpcWrapper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object NpcMenuDialog : KoinComponent {
    private val plugin: Plugin by inject()

    fun create(npcWrapper: NpcWrapper): Dialog = Dialog.create { builder ->
        val currentName = npcWrapper.customName()
            ?.let { MiniMessage.miniMessage().serialize(it) }
            .orEmpty()

        builder.empty()
            .base(
                DialogBase.builder(
                    Component.text()
                        .append(npcWrapper.npcType.iconComponent())
                        .appendSpace()
                        .append(npcWrapper.customName() ?: npcWrapper.npcType.defaultName())
                        .build()
                )
                    .inputs(
                        listOf(
                            DialogInput.text(
                                "name",
                                Component.translatable(NpcTranslations.NPC_NAME),
                            )
                                .initial(currentName)
                                .maxLength(32)
                                .build(),
                            DialogInput.singleOption(
                                "mode",
                                Component.translatable(NpcTranslations.NPC_MODE),
                                NpcMode.entries.map { npcMode ->
                                    SingleOptionDialogInput.OptionEntry.create(
                                        npcMode.key.value(),
                                        Component.translatable(npcMode.translationKey()),
                                        npcWrapper.mode == npcMode,
                                    )
                                }
                            ).build(),
                        )
                    ).build()
            )
            .type(
                DialogType.multiAction(
                    listOf(
                        ActionButton.builder(Component.translatable(NpcTranslations.DIALOG_VANILIFE_NPC_MENU_DONE))
                            .action(
                                DialogAction.customClick(
                                    { response, _ ->
                                        npcWrapper.delegate.scheduler.run(
                                            plugin,
                                            {
                                                updateName(npcWrapper, response.getText("name"))
                                                updateMode(npcWrapper, response.getText("mode"))
                                            },
                                            null,
                                        )
                                    },
                                    ClickCallback.Options.builder()
                                        .uses(ClickCallback.UNLIMITED_USES)
                                        .lifetime(ClickCallback.DEFAULT_LIFETIME)
                                        .build()
                                )
                            )
                            .build(),
                    )
                ).build()
            )
    }

    private fun updateName(npcWrapper: NpcWrapper, value: String?) {
        val trimmed = value?.trim().orEmpty()
        if (trimmed.isEmpty()) {
            npcWrapper.customName(null)
            npcWrapper.delegate.isCustomNameVisible = false
        } else {
            npcWrapper.customName(MiniMessage.miniMessage().deserialize(trimmed))
            npcWrapper.delegate.isCustomNameVisible = true
        }
    }

    private fun updateMode(npcWrapper: NpcWrapper, value: String?) {
        val mode = value?.let(NpcMode::byValue) ?: return
        npcWrapper.mode = mode
    }
}
