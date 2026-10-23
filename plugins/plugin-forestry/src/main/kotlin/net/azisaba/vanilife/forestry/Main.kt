package net.azisaba.vanilife.forestry

import com.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import net.azisaba.vanilife.forestry.timber.TimberAnimator
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class Main : JavaPlugin() {
    private lateinit var koinApp: KoinApplication

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

        koinApp = startKoin {
            modules(module {
                single<Plugin> { this@Main }
                single { TimberAnimator() }
            })
        }

        setupEventListeners(koinApp.koin)
    }

    override fun onDisable() {
        if (::koinApp.isInitialized) {
            koinApp.close()
        }
        if (PacketEvents.getAPI()?.isInitialized == true && server.pluginManager.getPlugin("packetevents") == null) {
            PacketEvents.getAPI().terminate()
        }
    }
}
