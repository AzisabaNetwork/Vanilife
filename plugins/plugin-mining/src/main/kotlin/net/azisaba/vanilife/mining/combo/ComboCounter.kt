package net.azisaba.vanilife.mining.combo

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.azisaba.vanilife.mining.MiningBlockTypeTags
import org.bukkit.block.Block
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class ComboCounter internal constructor() {
    private val combo: AtomicInteger = AtomicInteger(0)

    private val lastMiningTime: AtomicLong = AtomicLong(0L)

    fun read(): Int {
        val now = System.currentTimeMillis()
        return if (!isExpired(now)) combo.get() else {
            combo.set(0)
            0
        }
    }

    fun increment() {
        val now = System.currentTimeMillis()
        if (isExpired(now)) {
            combo.set(0)
        }
        combo.incrementAndGet()
        lastMiningTime.set(now)
    }

    private fun isExpired(now: Long): Boolean = now - lastMiningTime.get() > COMBO_TIMEOUT_MILLIS

    companion object {
        private const val COMBO_TIMEOUT_TICKS: Long = 20L * 10
        private const val COMBO_TIMEOUT_MILLIS: Long = COMBO_TIMEOUT_TICKS * 50L

        fun shouldIncrementCombo(block: Block): Boolean = RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.BLOCK)
            .getTag(MiningBlockTypeTags.ORES)
            .contains(block.type.asBlockType()!!.key())
    }
}
