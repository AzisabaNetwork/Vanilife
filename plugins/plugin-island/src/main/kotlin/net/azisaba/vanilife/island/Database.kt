package net.azisaba.vanilife.island

import net.azisaba.vanilife.island.enchantment.IslandEnchantmentsTable
import net.azisaba.vanilife.island.members.IslandMembersTable
import net.azisaba.vanilife.island.storage.IslandStorageTable
import net.azisaba.vanilife.island.visitors.IslandBansTable
import net.azisaba.vanilife.island.visitors.IslandVisitorsTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

internal fun Database.setupTables(): Database = transaction(this) {
    SchemaUtils.create(
        IslandsTable,
        IslandEnchantmentsTable,
        IslandMembersTable,
        IslandStorageTable,
        IslandVisitorsTable,
        IslandBansTable,
    )
    SchemaUtils.addMissingColumnsStatements(
        IslandsTable,
        IslandEnchantmentsTable,
        IslandMembersTable,
        IslandStorageTable,
        IslandVisitorsTable,
        IslandBansTable,
    ).forEach(::exec)
    this@setupTables
}
