package dev.Error110.itemsRepairAddon

import dev.Error110.itemsRepairAddon.config.PluginConfig
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

object ItemsRepairAddon {

    var plugin : Plugin? = null
    var logger : Logger? = null
    var config : PluginConfig? = null

    fun init(p: Plugin, c : PluginConfig) {
        plugin = p
        logger = p.logger
        config = c
        p.logger.info("Config loaded with version ${config!!.configVersion}")
    }
}