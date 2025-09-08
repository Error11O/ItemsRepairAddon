package dev.Error110.itemsRepairAddon.commands

import dev.Error110.itemsRepairAddon.ItemsRepairAddon
import dev.Error110.itemsRepairAddon.ItemsRepairAddonPlugin
import net.Indyuce.mmoitems.MMOItems
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.*
import org.bukkit.entity.Player

class Commands : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args == null || args.isEmpty()) {
            sender.sendMessage("Usage: /$label <reload|info|hand>")
            return true
        }

        // Permission check for players
        if (sender is Player && !sender.hasPermission("itemsrepairaddon.admin")) {
            sender.sendMessage("${ChatColor.RED}You do not have permission to use this command.")
            return true
        }

        when (args[0].lowercase()) {
            "reload" -> {
                val plugin = Bukkit.getPluginManager().getPlugin("ItemsRepairAddon") as? ItemsRepairAddonPlugin
                if (plugin != null) {
                    plugin.reload()
                    sender.sendMessage("Config reloaded.")
                } else {
                    sender.sendMessage("Plugin instance not found.")
                }
            }

            "info" -> {
                if (sender !is Player) return true
                val player = sender
                val item = player.inventory.itemInMainHand
                if (item == null) {
                    sender.sendMessage("${ChatColor.RED}You must be holding an item to use this command.")
                    return true
                }
                val type = MMOItems.getType(item)
                val id = MMOItems.getID(item)

                if (type == null || id == null) {
                    sender.sendMessage("${ChatColor.RED}This item is not an MMOItem.")
                    return true
                }
                sender.sendMessage("${ChatColor.GREEN}Item Type: ${type.name}, Item ID: $id")
            }

            "hand" -> {
                val player: Player? = when (sender) {
                    is Player -> sender
                    is ConsoleCommandSender -> {
                        if (args.size < 2) {
                            sender.sendMessage("${ChatColor.RED}Usage: /$label hand <player>")
                            return true
                        }
                        val target = Bukkit.getPlayer(args[1])
                        if (target == null) {
                            sender.sendMessage("${ChatColor.RED}Player not found.")
                            return true
                        }
                        target
                    }
                    else -> {
                        sender.sendMessage("${ChatColor.RED}This command can only be used by a player or console.")
                        return true
                    }
                }

                val item = player!!.inventory.itemInMainHand
                if (item == null || item.type.isAir) {
                    sender.sendMessage("${ChatColor.RED}You must be holding an item to use this command.")
                    return true
                }
                if (item.durability.toInt() >= item.type.maxDurability) {
                    sender.sendMessage("${ChatColor.RED}This item does not need repair.")
                    return true
                }
                var repairable = false
                for (configType in ItemsRepairAddon.config!!.repairableItemsTypes) {
                    val type = MMOItems.getType(item)
                    if (type != null && type.name == configType) {
                        repairable = true; break
                    }
                }
                if (!repairable) {
                    sender.sendMessage("${ChatColor.RED}This item is not repairable.")
                    return true
                }
                item.durability = 0
                player.sendMessage("${ChatColor.GREEN}Item repaired successfully.")
                return true
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): List<String?>? {
        if (sender.isOp) {
            return listOf("reload", "info", "hand")
        }
        return emptyList()
    }
}