package dev.Error110.itemsRepairAddon.config

import org.bukkit.plugin.Plugin

object ConfigManager {

    fun load(plugin: Plugin) : PluginConfig {
        val c = plugin.config
        plugin.saveDefaultConfig()
        return PluginConfig(
            configVersion = c.getInt("configVersion", 1),
            repairableItemsTypes = c.getStringList("repairableItemsTypes")
        )
    }

    fun reload(plugin : Plugin) : PluginConfig {
        plugin.reloadConfig()
        return load(plugin)
    }

}