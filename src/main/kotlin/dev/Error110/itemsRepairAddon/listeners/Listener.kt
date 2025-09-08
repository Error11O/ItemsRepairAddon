package dev.Error110.itemsRepairAddon.listeners

import dev.Error110.itemsRepairAddon.ItemsRepairAddon
import dev.Error110.itemsRepairAddon.utils.PermUtils
import net.Indyuce.mmoitems.MMOItems
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.Damageable

class Listener : Listener {

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        event.drops.forEach { item ->
            ItemsRepairAddon.config!!.repairableItemsTypes.forEach { type ->
                if (MMOItems.getType(item)?.name == type) {
                    val randomNumber = (1..100).random()
                    if (randomNumber <= PermUtils.lowestLevel(event.player)) item.durability = (item.type.maxDurability - 1).toShort()
                }
            }
        }
    }

    // Prevent using items with 1 durability (right/left click)
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (item.durability.toInt() == 1) {
            event.player.sendMessage("${ChatColor.RED} this item is too damaged to be used!")
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        val player = event.damager as? Player ?: return
        val item = player.inventory.itemInMainHand
        if (item.type.maxDurability > 0) {
            val meta = item.itemMeta
            val damage = if (meta is Damageable) meta.damage else item.durability.toInt()
            if (damage >= item.type.maxDurability - 1) {
                player.sendMessage("${ChatColor.RED} this item is too damaged to be used!")
                event.isCancelled = true
            }
        }
    }
}