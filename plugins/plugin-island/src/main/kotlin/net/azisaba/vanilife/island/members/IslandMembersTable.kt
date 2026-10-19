package net.azisaba.vanilife.island.members

import net.azisaba.vanilife.island.IslandsTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.java.javaUUID
import java.util.*

object IslandMembersTable : Table("island_members") {
    val position: Column<EntityID<Long>> = reference("position", IslandsTable)

    val member: Column<UUID> = javaUUID("member")

    override val primaryKey: PrimaryKey = PrimaryKey(position, member)
}
