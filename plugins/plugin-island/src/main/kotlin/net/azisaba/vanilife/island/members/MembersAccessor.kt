package net.azisaba.vanilife.island.members

import net.azisaba.vanilife.world.IslandPosition
import org.bukkit.OfflinePlayer
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface MembersAccessor {
    val members: Set<UUID>

    fun isMember(uuid: UUID): Boolean = uuid in members

    fun isMember(player: OfflinePlayer): Boolean = isMember(player.uniqueId)

    suspend fun addMember(uuid: UUID): Boolean

    suspend fun addMember(player: OfflinePlayer): Boolean = addMember(player.uniqueId)

    suspend fun removeMember(uuid: UUID): Boolean

    suspend fun removeMember(player: OfflinePlayer): Boolean = removeMember(player.uniqueId)

    @ApiStatus.Internal
    suspend fun bootstrapMembers()

    companion object {
        fun fromDatabase(position: IslandPosition, database: Database): MembersAccessor =
            MembersAccessorImpl(position, database)
    }
}

private class MembersAccessorImpl(
    position: IslandPosition,
    private val database: Database,
) : MembersAccessor {
    override val members: Set<UUID>
        get() = requireLoaded().toSet()

    private val positionId: Long = position.toLong()

    private var cacheSet: MutableSet<UUID>? = null

    override suspend fun addMember(uuid: UUID): Boolean = suspendTransaction(database) {
        val inserted = IslandMembersTable.insertIgnore {
            it[IslandMembersTable.position] = positionId
            it[IslandMembersTable.member] = uuid
        }.insertedCount > 0
        requireLoaded().add(uuid)
        inserted
    }

    override suspend fun removeMember(uuid: UUID): Boolean = suspendTransaction(database) {
        requireLoaded().remove(uuid)
        IslandMembersTable.deleteWhere {
            (IslandMembersTable.position eq positionId) and (IslandMembersTable.member eq uuid)
        } > 0
    }

    override suspend fun bootstrapMembers() = suspendTransaction(database) {
        val loaded = ConcurrentHashMap.newKeySet<UUID>()
        IslandMembersTable.select(IslandMembersTable.member)
            .where { IslandMembersTable.position eq positionId }
            .forEach { row ->
                loaded.add(row[IslandMembersTable.member])
            }
        cacheSet = loaded
    }

    private fun requireLoaded(): MutableSet<UUID> = cacheSet
        ?: throw IllegalStateException("Members have not yet been loaded")
}
