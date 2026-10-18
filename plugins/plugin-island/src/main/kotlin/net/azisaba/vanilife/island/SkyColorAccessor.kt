package net.azisaba.vanilife.island

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState
import net.azisaba.vanilife.world.IslandPosition
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update

interface SkyColorAccessor {
    val skyRainLevel: Float

    val skyThunderLevel: Float

    suspend fun skyColor(rainLevel: Float, thunderLevel: Float)

    fun sendSkyColor(player: Player)

    fun resetSkyColor(player: Player)

    suspend fun bootstrapSkyColorData()

    companion object {
        fun fromDatabase(position: IslandPosition, database: Database): SkyColorAccessor =
            SkyColorAccessorImpl(position, database)
    }
}

private class SkyColorAccessorImpl(
    private val position: IslandPosition,
    private val database: Database,
) : SkyColorAccessor {
    override val skyRainLevel: Float
        get() = requireLoaded().rainLevel

    override val skyThunderLevel: Float
        get() = requireLoaded().thunderLevel

    private val positionId: Long = position.toLong()

    private var cacheData: CacheData? = null

    private val columns: List<Column<*>> = listOf(
        IslandsTable.skyRainLevel,
        IslandsTable.skyThunderLevel,
    )

    override suspend fun skyColor(rainLevel: Float, thunderLevel: Float) = suspendTransaction(database) {
        val clampedRain = rainLevel.coerceIn(0f, 10f)
        val clampedThunder = thunderLevel.coerceIn(0f, 10f)

        IslandsTable.update(where = { IslandsTable.id eq positionId }) {
            it[IslandsTable.skyRainLevel] = clampedRain
            it[IslandsTable.skyThunderLevel] = clampedThunder
        }

        cacheData = CacheData(clampedRain, clampedThunder)
    }

    override fun sendSkyColor(player: Player) {
        val data = requireLoaded()
        sendPackets(player, data.rainLevel, data.thunderLevel)
    }

    override fun resetSkyColor(player: Player) {
        val data = cacheData ?: return
        if (data.rainLevel != 0f || data.thunderLevel != 0f) {
            sendPackets(player, 0f, 0f)
        }
    }

    override suspend fun bootstrapSkyColorData() = suspendTransaction(database) {
        val row = IslandsTable.select(columns)
            .where { IslandsTable.id eq positionId }
            .single()

        cacheData = CacheData(
            rainLevel = row[IslandsTable.skyRainLevel],
            thunderLevel = row[IslandsTable.skyThunderLevel],
        )
    }

    private fun sendPackets(player: Player, rainLevel: Float, thunderLevel: Float) {
        val playerManager = PacketEvents.getAPI().playerManager
        playerManager.sendPacket(
            player,
            WrapperPlayServerChangeGameState(
                WrapperPlayServerChangeGameState.Reason.RAIN_LEVEL_CHANGE,
                rainLevel,
            ),
        )
        playerManager.sendPacket(
            player,
            WrapperPlayServerChangeGameState(
                WrapperPlayServerChangeGameState.Reason.THUNDER_LEVEL_CHANGE,
                thunderLevel,
            ),
        )
    }

    private fun requireLoaded(): CacheData = cacheData
        ?: throw IllegalStateException("Sky color data has not yet been loaded")

    private data class CacheData(
        val rainLevel: Float,
        val thunderLevel: Float,
    )
}
