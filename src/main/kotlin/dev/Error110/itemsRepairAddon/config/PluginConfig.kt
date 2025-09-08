package dev.Error110.itemsRepairAddon.config

data class PluginConfig(
    val configVersion : Int = 1,
    val repairableItemsTypes : List<String> = emptyList()
)