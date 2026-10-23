package net.azisaba.vanilife.runtime

import com.github.retrooper.packetevents.PacketEvents
import me.tofaa.entitylib.APIConfig
import me.tofaa.entitylib.EntityLib
import org.bukkit.plugin.java.JavaPlugin

internal class Main : JavaPlugin() {
    override fun onEnable() {
        // EntityLib keeps a single global instance, so it is initialized here once for every Vanilife plugin
        EntityLib.init(APIConfig(PacketEvents.getAPI(), VanilifeEntityLibPlatform(this)))
    }

    override fun onDisable() {
        EntityLib.shutdown()
    }
}
