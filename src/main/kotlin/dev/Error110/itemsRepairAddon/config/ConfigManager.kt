package dev.Error110.itemsRepairAddon.config

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.Plugin

data class LoreConfig(
    val title: String = "BROKEN ITEM",
    val lines: List<String> = emptyList()
)

data class PluginConfig(
    val configVersion: Int = 1,
    val disabledMessage : String = "This item is too damaged to be used!",
    val loreDisableText: String = "BROKEN",
    val lore: LoreConfig = LoreConfig(),
    val repairableItemsTypes: List<String> = emptyList()
)

object ConfigManager {

    fun load(plugin: Plugin): PluginConfig {
        plugin.saveDefaultConfig()
        val c = plugin.config

        val version = when {
            c.contains("configVersion") -> c.getInt("configVersion", 1)
            c.contains("config_version") -> c.getInt("config_version", 1)
            else -> 1
        }
        val disabledMessage = c.getString("disabled_message", "This item is too damaged to be used!") ?: "This item is too damaged to be used!"

        val loreSection: ConfigurationSection? = c.getConfigurationSection("lore")
        val lore = loreSection?.let {
            LoreConfig(
                title = it.getString("title", "BROKEN ITEM") ?: "BROKEN ITEM",
                lines = it.getStringList("lines")
            )
        } ?: LoreConfig()

        return PluginConfig(
            configVersion = version,
            disabledMessage = disabledMessage,
            loreDisableText = c.getString("loreDisableText", "BROKEN") ?: "BROKEN",
            lore = lore,
            repairableItemsTypes = c.getStringList("repairableItemsTypes")
        )
    }

    fun reload(plugin: Plugin): PluginConfig {
        plugin.reloadConfig()
        return load(plugin)
    }
}