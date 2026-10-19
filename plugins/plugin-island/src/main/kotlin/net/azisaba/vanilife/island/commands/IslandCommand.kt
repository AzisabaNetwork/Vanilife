package net.azisaba.vanilife.island.commands

import com.github.shynixn.mccoroutine.folia.launch
import com.mojang.brigadier.Command
import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import net.azisaba.vanilife.island.Island
import net.azisaba.vanilife.island.cache.IslandCacheMap
import net.azisaba.vanilife.island.currentIsland
import net.azisaba.vanilife.island.teleport
import net.azisaba.vanilife.island.wrack.WrackType
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

internal object IslandCommand : KoinComponent {
    private val plugin: Plugin by inject()
    private val cacheMap: IslandCacheMap by inject()

    private val MUST_BE_PLAYER = SimpleCommandExceptionType(LiteralMessage("A player is required"))
    private val INVALID_ENCHANTMENT = SimpleCommandExceptionType(LiteralMessage("Invalid enchantment key"))

    fun create(): LiteralCommandNode<CommandSourceStack> = Commands.literal("island")
        .requires(Commands.restricted { it.sender.hasPermission("vanilife.command.island") })
        .then(
            Commands.literal("info")
                .executes(::infoSelf)
                .then(
                    Commands.argument("target", StringArgumentType.word())
                        .suggests(::suggestPlayers)
                        .executes(::infoTarget)
                )
        )
        .then(
            Commands.literal("tp")
                .then(
                    Commands.argument("target", StringArgumentType.word())
                        .suggests(::suggestPlayers)
                        .executes(::teleportToIsland)
                )
        )
        .then(
            Commands.literal("level")
                .then(
                    Commands.literal("get")
                        .then(
                            Commands.argument("target", StringArgumentType.word())
                                .suggests(::suggestPlayers)
                                .executes(::getLevel)
                        )
                )
                .then(
                    Commands.literal("set")
                        .then(
                            Commands.argument("target", StringArgumentType.word())
                                .suggests(::suggestPlayers)
                                .then(
                                    Commands.argument("level", IntegerArgumentType.integer(Island.MIN_LEVEL, Island.MAX_LEVEL))
                                        .executes(::setLevel)
                                )
                        )
                )
        )
        .then(
            Commands.literal("score")
                .then(
                    Commands.literal("get")
                        .then(
                            Commands.argument("target", StringArgumentType.word())
                                .suggests(::suggestPlayers)
                                .executes(::getScore)
                        )
                )
                .then(
                    Commands.literal("set")
                        .then(
                            Commands.argument("target", StringArgumentType.word())
                                .suggests(::suggestPlayers)
                                .then(
                                    Commands.argument("score", DoubleArgumentType.doubleArg(0.0))
                                        .executes(::setScore)
                                )
                        )
                )
                .then(
                    Commands.literal("add")
                        .then(
                            Commands.argument("target", StringArgumentType.word())
                                .suggests(::suggestPlayers)
                                .then(
                                    Commands.argument("score", DoubleArgumentType.doubleArg())
                                        .executes(::addScore)
                                )
                        )
                )
        )
        .then(
            Commands.literal("enchantment")
                .then(
                    Commands.literal("list")
                        .then(
                            Commands.argument("target", StringArgumentType.word())
                                .suggests(::suggestPlayers)
                                .executes(::listEnchantments)
                        )
                )
                .then(
                    Commands.literal("add")
                        .then(
                            Commands.argument("target", StringArgumentType.word())
                                .suggests(::suggestPlayers)
                                .then(
                                    Commands.argument("enchantment", ArgumentTypes.key())
                                        .suggests(::suggestEnchantments)
                                        .executes(::addEnchantment)
                                )
                        )
                )
                .then(
                    Commands.literal("remove")
                        .then(
                            Commands.argument("target", StringArgumentType.word())
                                .suggests(::suggestPlayers)
                                .then(
                                    Commands.argument("enchantment", ArgumentTypes.key())
                                        .suggests(::suggestEnchantments)
                                        .executes(::removeEnchantment)
                                )
                        )
                )
                .then(
                    Commands.literal("clear")
                        .then(
                            Commands.argument("target", StringArgumentType.word())
                                .suggests(::suggestPlayers)
                                .executes(::clearEnchantments)
                        )
                )
        )
        .then(
            Commands.literal("wrack")
                .then(
                    Commands.argument("target", StringArgumentType.word())
                        .suggests(::suggestPlayers)
                        .executes(::spawnWrack)
                )
        )
        .build()

    private fun infoSelf(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.sender as? Player ?: throw MUST_BE_PLAYER.create()
        val sender = context.source.sender
        plugin.launch {
            val island = player.currentIsland ?: cacheMap.lookup(player.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("Island not found.", NamedTextColor.RED))
                return@launch
            }
            sendIslandInfo(sender, island)
        }
        return Command.SINGLE_SUCCESS
    }

    private fun infoTarget(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            sendIslandInfo(sender, island)
        }
        return Command.SINGLE_SUCCESS
    }

    private fun sendIslandInfo(sender: org.bukkit.command.CommandSender, island: Island) {
        val ownerName = Bukkit.getOfflinePlayer(island.owner).name ?: island.owner.toString()
        val memberNames = island.members.map { Bukkit.getOfflinePlayer(it).name ?: it.toString().take(8) }
        val enchantmentKeys = island.enchantments.map { it.key().asString() }

        sender.sendMessage(
            Component.text()
                .append(Component.text("=== Island Info (${ownerName}) ===", NamedTextColor.GOLD))
                .appendNewline()
                .append(Component.text("Position: (${island.position.x()}, ${island.position.z()}) [center: ${island.position.centerBlockX()}, ${island.position.centerBlockZ()}]", NamedTextColor.GRAY))
                .appendNewline()
                .append(Component.text("Display Name: ", NamedTextColor.GRAY))
                .append(island.displayName)
                .appendNewline()
                .append(Component.text("Level: ${island.level} | Score: ${island.score}", NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Access: ${if (island.isVisitable) "Public" else "Private"}", NamedTextColor.AQUA))
                .appendNewline()
                .append(Component.text("Members (${memberNames.size}): ${memberNames.joinToString(", ").ifEmpty { "None" }}", NamedTextColor.YELLOW))
                .appendNewline()
                .append(Component.text("Enchantments (${enchantmentKeys.size}): ${enchantmentKeys.joinToString(", ").ifEmpty { "None" }}", NamedTextColor.LIGHT_PURPLE))
                .build()
        )
    }

    private fun teleportToIsland(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.sender as? Player ?: throw MUST_BE_PLAYER.create()
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                player.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            player.teleport(island)
            player.sendMessage(Component.text("Teleported to ${target.name ?: target.uniqueId}'s island.", NamedTextColor.GREEN))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun getLevel(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            sender.sendMessage(Component.text("${target.name ?: target.uniqueId}'s island level is ${island.level}.", NamedTextColor.GREEN))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun setLevel(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val level = IntegerArgumentType.getInteger(context, "level")
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            island.level(level)
            sender.sendMessage(Component.text("Set ${target.name ?: target.uniqueId}'s island level to $level.", NamedTextColor.GREEN))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun getScore(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            sender.sendMessage(Component.text("${target.name ?: target.uniqueId}'s island score is ${island.score}.", NamedTextColor.GREEN))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun setScore(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val score = DoubleArgumentType.getDouble(context, "score")
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            island.score(score)
            sender.sendMessage(Component.text("Set ${target.name ?: target.uniqueId}'s island score to $score.", NamedTextColor.GREEN))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun addScore(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val delta = DoubleArgumentType.getDouble(context, "score")
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            val newScore = (island.score + delta).coerceAtLeast(0.0)
            island.score(newScore)
            sender.sendMessage(Component.text("Updated ${target.name ?: target.uniqueId}'s island score to $newScore.", NamedTextColor.GREEN))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun listEnchantments(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            val list = island.enchantments.map { it.key().asString() }.sorted()
            sender.sendMessage(
                Component.text(
                    "${target.name ?: target.uniqueId}'s island enchantments (${list.size}): ${list.joinToString(", ").ifEmpty { "None" }}",
                    NamedTextColor.LIGHT_PURPLE,
                )
            )
        }
        return Command.SINGLE_SUCCESS
    }

    private fun addEnchantment(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val enchantmentKey = ensureEnchantmentKey(context)
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            island.addEnchantment(enchantmentKey)
            sender.sendMessage(
                Component.text("Added enchantment ${enchantmentKey.key().asString()} to ${target.name ?: target.uniqueId}'s island.", NamedTextColor.GREEN)
            )
        }
        return Command.SINGLE_SUCCESS
    }

    private fun removeEnchantment(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val enchantmentKey = ensureEnchantmentKey(context)
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            island.removeEnchantment(enchantmentKey)
            sender.sendMessage(
                Component.text("Removed enchantment ${enchantmentKey.key().asString()} from ${target.name ?: target.uniqueId}'s island.", NamedTextColor.YELLOW)
            )
        }
        return Command.SINGLE_SUCCESS
    }

    private fun clearEnchantments(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            island.clearEnchantments()
            sender.sendMessage(Component.text("Cleared all enchantments from ${target.name ?: target.uniqueId}'s island.", NamedTextColor.YELLOW))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun spawnWrack(context: CommandContext<CommandSourceStack>): Int {
        val target = resolveOfflinePlayer(context) ?: return sendPlayerNotFound(context)
        val sender = context.source.sender
        plugin.launch {
            val island = cacheMap.lookup(target.uniqueId)
            if (island == null) {
                sender.sendMessage(Component.text("No island found for ${target.name ?: target.uniqueId}.", NamedTextColor.RED))
                return@launch
            }
            val wrackType = WrackType.roll(Random.Default, island.level, island)
                ?: WrackType.byLevel(island.level).randomOrNull()
                ?: WrackType.firstOrNull()
            if (wrackType == null) {
                sender.sendMessage(Component.text("No wrack types registered.", NamedTextColor.RED))
                return@launch
            }
            island.spawnWrack(wrackType)
            sender.sendMessage(Component.text("Spawned wrack on ${target.name ?: target.uniqueId}'s island.", NamedTextColor.GREEN))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun resolveOfflinePlayer(context: CommandContext<CommandSourceStack>): OfflinePlayer? {
        val name = StringArgumentType.getString(context, "target")
        return Bukkit.getPlayerExact(name) ?: Bukkit.getOfflinePlayerIfCached(name)
    }

    private fun sendPlayerNotFound(context: CommandContext<CommandSourceStack>): Int {
        val name = StringArgumentType.getString(context, "target")
        context.source.sender.sendMessage(Component.text("Player '$name' not found.", NamedTextColor.RED))
        return 0
    }

    private fun ensureEnchantmentKey(context: CommandContext<CommandSourceStack>): TypedKey<Enchantment> {
        val key = context.getArgument("enchantment", Key::class.java)
        val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
        val typedKey = RegistryKey.ENCHANTMENT.typedKey(key)
        if (registry.get(typedKey) == null) {
            throw INVALID_ENCHANTMENT.create()
        }
        return typedKey
    }

    private fun suggestPlayers(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val remaining = builder.remainingLowerCase
        Bukkit.getOnlinePlayers()
            .map { it.name }
            .filter { it.lowercase().startsWith(remaining) }
            .sorted()
            .forEach(builder::suggest)
        return builder.buildFuture()
    }

    private fun suggestEnchantments(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val remaining = builder.remainingLowerCase
        val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
        registry.keyStream()
            .map { it.asString() }
            .filter { it.startsWith(remaining) || it.removePrefix("minecraft:").startsWith(remaining) || it.removePrefix("vanilife:").startsWith(remaining) }
            .sorted()
            .forEach(builder::suggest)
        return builder.buildFuture()
    }
}
