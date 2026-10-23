package net.azisaba.vanilife.island.event

import net.azisaba.vanilife.island.Island
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

// Islands are initialized both on tick threads (join) and off them (async spawn location lookup),
// so the event is marked asynchronous whenever it is fired from a non-tick thread
data class IslandInitEvent(val island: Island) : Event(!Bukkit.isPrimaryThread()) {
    override fun getHandlers(): HandlerList = HANDLER_LIST

    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLER_LIST
    }
}
