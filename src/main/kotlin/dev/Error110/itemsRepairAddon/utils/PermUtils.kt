package dev.Error110.itemsRepairAddon.utils

import org.bukkit.permissions.Permissible

object PermUtils {

    private const val BASE = "itemsrepairaddon"

    // Checks specific level (supports wildcard)
    fun hasLevel(p: Permissible, level: Int): Boolean =
        p.hasPermission("$BASE.$level") || p.hasPermission("$BASE.*")

    // Highest level the player has (50 if none)
    fun highestLevel(p: Permissible, min: Int = 1, max: Int = 300): Int =
        (max downTo min).firstOrNull { hasLevel(p, it) } ?: 50
}