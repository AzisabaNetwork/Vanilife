package net.azisaba.vanilife.mining

import net.azisaba.vanilife.mining.listener.AutoSmeltListener
import net.azisaba.vanilife.mining.listener.ComboListener
import net.azisaba.vanilife.mining.listener.FrozenOreMiningListener
import net.azisaba.vanilife.mining.listener.MinerListener

internal fun Main.setupEventListeners() {
    server.pluginManager.registerEvents(ComboListener, this)
    server.pluginManager.registerEvents(MinerListener(this), this)
    server.pluginManager.registerEvents(AutoSmeltListener(MiningEnchantments.AUTO_SMELT), this)

    val coreProtect = server.pluginManager.getPlugin("CoreProtect")
    val placedBlockLookup = if (coreProtect != null && coreProtect.isEnabled) {
        CoreProtectPlacedBlockLookup.create(coreProtect)
    } else {
        logger.warning("CoreProtect is not enabled; frozen ores will also drop from player-placed ore blocks")
        PlacedBlockLookup.NONE
    }
    server.pluginManager.registerEvents(
        FrozenOreMiningListener(MiningBiomeTags.DROPS_FROZEN_ORES, placedBlockLookup),
        this
    )
}
