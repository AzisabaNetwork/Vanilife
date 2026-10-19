package net.azisaba.vanilife.island.visitors

import net.azisaba.vanilife.world.IslandPosition
import org.bukkit.OfflinePlayer
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.sum
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

interface VisitorsAccessor {
    val bannedPlayers: Set<UUID>

    fun isBanned(uuid: UUID): Boolean = uuid in bannedPlayers

    fun isBanned(player: OfflinePlayer): Boolean = isBanned(player.uniqueId)

    suspend fun banVisitor(uuid: UUID): Boolean

    suspend fun banVisitor(player: OfflinePlayer): Boolean = banVisitor(player.uniqueId)

    suspend fun unbanVisitor(uuid: UUID): Boolean

    suspend fun unbanVisitor(player: OfflinePlayer): Boolean = unbanVisitor(player.uniqueId)

    suspend fun beginVisit(uuid: UUID)

    suspend fun beginVisit(player: OfflinePlayer) = beginVisit(player.uniqueId)

    suspend fun endVisit(uuid: UUID)

    suspend fun endVisit(player: OfflinePlayer) = endVisit(player.uniqueId)

    suspend fun firstVisitAt(uuid: UUID): Instant?

    suspend fun firstVisitAt(player: OfflinePlayer): Instant? = firstVisitAt(player.uniqueId)

    suspend fun lastVisitAt(uuid: UUID): Instant?

    suspend fun lastVisitAt(player: OfflinePlayer): Instant? = lastVisitAt(player.uniqueId)

    suspend fun stayTimeOf(uuid: UUID): Duration

    suspend fun stayTimeOf(player: OfflinePlayer): Duration = stayTimeOf(player.uniqueId)

    suspend fun totalStayTime(): Duration

    suspend fun hasVisited(uuid: UUID): Boolean

    suspend fun hasVisited(player: OfflinePlayer): Boolean = hasVisited(player.uniqueId)

    suspend fun visitors(): Set<UUID>

    @ApiStatus.Internal
    suspend fun bootstrapVisitors()

    companion object {
        fun fromDatabase(position: IslandPosition, database: Database): VisitorsAccessor =
            VisitorsAccessorImpl(position, database)
    }
}

private class VisitorsAccessorImpl(
    private val position: IslandPosition, private val database: Database,
) : VisitorsAccessor {
    override val bannedPlayers: Set<UUID>
        get() = requireBannedSet().toSet()

    private val positionId: Long = position.toLong()

    private var bannedSet: MutableSet<UUID>? = null

    override suspend fun banVisitor(uuid: UUID): Boolean = suspendTransaction(database) {
        val inserted = IslandBansTable.insertIgnore {
            it[IslandBansTable.position] = positionId
            it[IslandBansTable.player] = uuid
        }.insertedCount > 0
        requireBannedSet().add(uuid)
        inserted
    }

    override suspend fun unbanVisitor(uuid: UUID): Boolean = suspendTransaction(database) {
        requireBannedSet().remove(uuid)
        IslandBansTable.deleteWhere {
            (IslandBansTable.position eq positionId) and (IslandBansTable.player eq uuid)
        } > 0
    }

    override suspend fun beginVisit(uuid: UUID) = suspendTransaction(database) {
        val now = Clock.System.now()

        IslandVisitorsTable.insertIgnore {
            it[position] = this@VisitorsAccessorImpl.position.toLong()
            it[visitor] = uuid
            it[firstVisitAt] = now
            it[lastVisitAt] = now
            it[stayTime] = Duration.ZERO
        }

        IslandVisitorsTable.update(where = { (IslandVisitorsTable.position eq position.toLong()) and (IslandVisitorsTable.visitor eq uuid) }) {
            it[lastVisitAt] = now
        }

        Unit
    }

    override suspend fun endVisit(uuid: UUID) = suspendTransaction(database) {
        val now = Clock.System.now()

        val row = IslandVisitorsTable.select(IslandVisitorsTable.lastVisitAt, IslandVisitorsTable.stayTime)
            .where { (IslandVisitorsTable.position eq position.toLong()) and (IslandVisitorsTable.visitor eq uuid) }
            .singleOrNull() ?: return@suspendTransaction

        val lastVisit = row[IslandVisitorsTable.lastVisitAt]
        val currentStayTime = row[IslandVisitorsTable.stayTime]

        val delta = now - lastVisit

        if (delta.isNegative()) return@suspendTransaction

        IslandVisitorsTable.update(where = { (IslandVisitorsTable.position eq position.toLong()) and (IslandVisitorsTable.visitor eq uuid) }) {
            it[IslandVisitorsTable.lastVisitAt] = now
            it[IslandVisitorsTable.stayTime] = currentStayTime + delta
        }
    }

    override suspend fun firstVisitAt(uuid: UUID): Instant? = suspendTransaction(database) {
        IslandVisitorsTable.select(IslandVisitorsTable.firstVisitAt)
            .where { (IslandVisitorsTable.position eq position.toLong()) and (IslandVisitorsTable.visitor eq uuid) }
            .singleOrNull()
            ?.get(IslandVisitorsTable.firstVisitAt)
    }

    override suspend fun lastVisitAt(uuid: UUID): Instant? = suspendTransaction(database) {
        IslandVisitorsTable.select(IslandVisitorsTable.lastVisitAt)
            .where { (IslandVisitorsTable.position eq position.toLong()) and (IslandVisitorsTable.visitor eq uuid) }
            .singleOrNull()
            ?.get(IslandVisitorsTable.lastVisitAt)
    }

    override suspend fun stayTimeOf(uuid: UUID): Duration = suspendTransaction(database) {
        IslandVisitorsTable.select(IslandVisitorsTable.stayTime)
            .where { (IslandVisitorsTable.position eq position.toLong()) and (IslandVisitorsTable.visitor eq uuid) }
            .singleOrNull()
            ?.get(IslandVisitorsTable.stayTime) ?: Duration.ZERO
    }

    override suspend fun totalStayTime(): Duration = suspendTransaction(database) {
        val sumExpression = IslandVisitorsTable.stayTime.sum()

        IslandVisitorsTable.select(sumExpression)
            .where { IslandVisitorsTable.position eq position.toLong() }
            .firstOrNull()
            ?.get(sumExpression) ?: Duration.ZERO
    }

    override suspend fun hasVisited(uuid: UUID): Boolean = suspendTransaction(database) {
        IslandVisitorsTable
            .select(IslandVisitorsTable.visitor)
            .where { (IslandVisitorsTable.position eq position.toLong()) and (IslandVisitorsTable.visitor eq uuid) }
            .any()
    }

    override suspend fun visitors(): Set<UUID> = suspendTransaction(database) {
        IslandVisitorsTable.select(IslandVisitorsTable.visitor)
            .where { IslandVisitorsTable.position eq position.toLong() }
            .map { it[IslandVisitorsTable.visitor] }
            .toSet()
    }

    override suspend fun bootstrapVisitors() = suspendTransaction(database) {
        val loaded = ConcurrentHashMap.newKeySet<UUID>()
        IslandBansTable.select(IslandBansTable.player)
            .where { IslandBansTable.position eq positionId }
            .forEach { row ->
                loaded.add(row[IslandBansTable.player])
            }
        bannedSet = loaded
    }

    private fun requireBannedSet(): MutableSet<UUID> = bannedSet
        ?: throw IllegalStateException("Visitors data has not yet been loaded")
}
