package net.azisaba.vanilife.enchanting

import com.github.retrooper.packetevents.PacketEvents
import com.github.shynixn.mccoroutine.folia.launch
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import net.azisaba.vanilife.reloadableConfig
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

internal class Main : JavaPlugin() {
    private lateinit var koinApp: KoinApplication

    override fun onLoad() {
        if (PacketEvents.getAPI()?.isLoaded != true) {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
            PacketEvents.getAPI().load()
        }
    }

    override fun onEnable() {
        val config = reloadableConfig(Configuration(), Configuration.serializer())
        val database = config.value().database.createConnection()

        if (!PacketEvents.getAPI().isInitialized) {
            PacketEvents.getAPI().init()
        }
        EnchantingRecipes.bootstrap(server)

        koinApp = startKoin {
            modules(
                module {
                    single<Plugin> { this@Main }
                    single<Database> { database }
                },
            )
        }

        EnchantingRecipe.bootstrap(this)
        setupEventListeners()

        launch {
            UnlockRateSource.bootstrap(database)
        }
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
