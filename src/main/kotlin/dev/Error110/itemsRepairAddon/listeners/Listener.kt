package dev.Error110.itemsRepairAddon.listeners

import dev.Error110.itemsRepairAddon.ItemsRepairAddon
import dev.Error110.itemsRepairAddon.utils.DisableUtils
import dev.Error110.itemsRepairAddon.utils.PermUtils
import io.lumine.mythic.lib.api.event.AttackEvent
import net.Indyuce.mmoitems.MMOItems
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class Listener : Listener {

    // this is the death event where we check the dropped items if they are mmoitems and if they are in the config list
    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        event.drops.forEach { item ->
            ItemsRepairAddon.config!!.repairableItemsTypes.forEach { type ->
                if (MMOItems.getType(item)?.name == type) {
                    val randomNumber = (1..100).random()
                    if (randomNumber <= PermUtils.highestLevel(event.player)) DisableUtils.addBroken(item)
                }
            }
        }
    }

    // this is the attack event from mythiclib to prevent attacking with broken items
    @EventHandler
    fun onAttack(event : AttackEvent) {
        if (!event.attack.isPlayer) return
        val player = event.attack.player
        val item = player.inventory.itemInMainHand
        if (DisableUtils.isBroken(item)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ItemsRepairAddon.config!!.disabledMessage))
            event.isCancelled = true
        }
    }

    // this is the interact event to prevent using broken items
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val usedItem = event.item ?: when (event.hand) {
            EquipmentSlot.OFF_HAND -> player.inventory.itemInOffHand
            else -> player.inventory.itemInMainHand
        }

        if (DisableUtils.isBroken(usedItem)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ItemsRepairAddon.config!!.disabledMessage))
            event.isCancelled = true
        }
    }

    // this is the interact entity event to prevent using broken items
    @EventHandler
    fun onInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        val usedItem = when (event.hand) {
            EquipmentSlot.OFF_HAND -> player.inventory.itemInOffHand
            else -> player.inventory.itemInMainHand
        }

        if (DisableUtils.isBroken(usedItem)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ItemsRepairAddon.config!!.disabledMessage))
            event.isCancelled = true
        }
    }

    // this is the entity damage by entity event to prevent attacking with broken items
    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        val player = event.damager as? Player ?: return
        val item = player.inventory.itemInMainHand
        if (DisableUtils.isBroken(item)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ItemsRepairAddon.config!!.disabledMessage))
            event.isCancelled = true
        }
    }

    // this is the block break event to prevent breaking blocks with broken items
    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        if (DisableUtils.isBroken(item)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ItemsRepairAddon.config!!.disabledMessage))
            event.isCancelled = true
        }
    }

    // this is the block place event to prevent placing blocks with broken items
    @EventHandler(ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val item = event.itemInHand
        if (DisableUtils.isBroken(item)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ItemsRepairAddon.config!!.disabledMessage))
            event.isCancelled = true
        }
    }
}