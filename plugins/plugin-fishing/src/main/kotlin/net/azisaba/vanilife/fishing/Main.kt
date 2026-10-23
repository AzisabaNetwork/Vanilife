package net.azisaba.vanilife.fishing

import com.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onLoad() {
        if (PacketEvents.getAPI()?.isLoaded != true) {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
            PacketEvents.getAPI().load()
        }
    }

    override fun onEnable() {
        if (!PacketEvents.getAPI().isInitialized) {
            PacketEvents.getAPI().init()
        }

        setupEventListeners()
    }

    override fun onDisable() {
        if (PacketEvents.getAPI()?.isInitialized == true && server.pluginManager.getPlugin("packetevents") == null) {
            PacketEvents.getAPI().terminate()
        }
    }
}
