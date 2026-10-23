package net.azisaba.vanilife.mining

import org.bukkit.block.Block

internal fun interface PlacedBlockLookup {
    fun isPlacedByPlayer(block: Block): Boolean

    companion object {
        val NONE: PlacedBlockLookup = PlacedBlockLookup { false }
    }
}
