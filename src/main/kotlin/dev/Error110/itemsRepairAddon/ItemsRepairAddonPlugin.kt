package dev.Error110.itemsRepairAddon

import dev.Error110.itemsRepairAddon.commands.Commands
import dev.Error110.itemsRepairAddon.config.ConfigManager
import dev.Error110.itemsRepairAddon.listeners.Listener
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

class ItemsRepairAddonPlugin : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        val config = ConfigManager.load(this)
        ItemsRepairAddon.init(this, config)
        server.pluginManager.registerEvents(Listener(), this)
        this.getCommand("mmorepair")?.setExecutor(Commands())
        this.getCommand("mmorepair")?.tabCompleter = Commands() as TabCompleter
    }

    fun reload(): Boolean {
        return try {
            val newConfig = ConfigManager.reload(this)
            ItemsRepairAddon.init(this, newConfig)
            logger.info("Config reloaded")
            true
        } catch (t: Throwable) {
            logger.severe("Config reload failed: ${t.message}")
            false
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
