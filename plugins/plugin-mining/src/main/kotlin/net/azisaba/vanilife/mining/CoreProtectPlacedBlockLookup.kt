package net.azisaba.vanilife.mining

import net.coreprotect.CoreProtect
import net.coreprotect.CoreProtectAPI
import org.bukkit.block.Block
import org.bukkit.plugin.Plugin

// Kept in its own class so CoreProtect classes are only loaded when the plugin is actually enabled
internal class CoreProtectPlacedBlockLookup private constructor(private val api: CoreProtectAPI) : PlacedBlockLookup {
    override fun isPlacedByPlayer(block: Block): Boolean {
        val lookup = api.blockLookup(block, 0) ?: return false
        return lookup.any { api.parseResult(it).actionId == 1 }
    }

    companion object {
        fun create(coreProtect: Plugin): PlacedBlockLookup = CoreProtectPlacedBlockLookup((coreProtect as CoreProtect).api)
    }
}
