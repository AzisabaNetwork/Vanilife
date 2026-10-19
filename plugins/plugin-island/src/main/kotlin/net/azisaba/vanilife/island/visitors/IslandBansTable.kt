package net.azisaba.vanilife.island.visitors

import net.azisaba.vanilife.island.IslandsTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.java.javaUUID
import java.util.*

object IslandBansTable : Table("island_bans") {
    val position: Column<EntityID<Long>> = reference("position", IslandsTable)

    val player: Column<UUID> = javaUUID("player")

    override val primaryKey: PrimaryKey = PrimaryKey(position, player)
}
