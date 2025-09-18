package dev.Error110.itemsRepairAddon.utils

import dev.Error110.itemsRepairAddon.ItemsRepairAddon
import net.Indyuce.mmoitems.api.ReforgeOptions
import net.Indyuce.mmoitems.api.util.MMOItemReforger
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object DisableUtils {

    private val PLAIN = PlainTextComponentSerializer.plainText()
    private val MINI = MiniMessage.miniMessage()
    private val LEGACY_AMP = LegacyComponentSerializer.legacyAmpersand()

    // Config title becomes the display name
    private val brokenTitleRaw: String
        get() = ItemsRepairAddon.config!!.lore.title

    // Config lines become the entire lore
    private val brokenLinesRaw: List<String>
        get() = ItemsRepairAddon.config!!.lore.lines

    private fun parseColored(input: String): Component {
        return if (input.indexOf('&') >= 0 || input.indexOf('§') >= 0) {
            LEGACY_AMP.deserialize(input)
        } else {
            MINI.deserialize(input)
        }
    }

    private fun parseColoredList(inputs: List<String>): List<Component> =
        inputs.map(::parseColored)

    private fun brokenLoreComponents(): List<Component> =
        parseColoredList(brokenLinesRaw.filter { it.isNotBlank() })

    fun addBroken(item: ItemStack): ItemStack {
        val meta = item.itemMeta ?: return item
        if (isBroken(item)) return item

        // Set display name to config title
        if (brokenTitleRaw.isNotBlank()) {
            meta.displayName(parseColored(brokenTitleRaw))
        }

        // Lore = ONLY configured lines (title excluded from lore)
        meta.lore(brokenLoreComponents())

        item.itemMeta = meta
        return item
    }

    fun isBroken(item: ItemStack): Boolean {
        val meta = item.itemMeta ?: return false

        val configuredNamePlain =
            brokenTitleRaw.takeIf { it.isNotBlank() }?.let { PLAIN.serialize(parseColored(it)).trim() }
        val itemNamePlain = meta.displayName()?.let { PLAIN.serialize(it).trim() }
        if (!configuredNamePlain.isNullOrBlank() && configuredNamePlain == itemNamePlain) {
            return true
        }

        val lore = meta.lore() ?: return false
        val plainLore = lore.map { PLAIN.serialize(it).trim() }

        val toMatch = brokenLinesRaw.map { it.trim() }.filter { it.isNotBlank() }
        if (toMatch.isEmpty() || plainLore.size < toMatch.size) return false

        outer@ for (i in 0..(plainLore.size - toMatch.size)) {
            for (j in toMatch.indices) {
                if (plainLore[i + j] != toMatch[j]) continue@outer
            }
            return true
        }
        return false
    }

    private fun defaultReforgeOptions(): ReforgeOptions {
        val conf = MemoryConfiguration().apply {
            set("reroll", false)
            set("upgrades", true)
            set("gemstones", true)
            set("modifications", true)
            set("skins", true)
            set("display-name", false)
        }
        return ReforgeOptions(conf)
    }

    @Throws(RegenerateException::class)
    fun removeDisabledName(
        player: Player,
        wildcard: Boolean = false,
        options: ReforgeOptions = defaultReforgeOptions()
    ): ItemStack? {
        val item = player.inventory.itemInMainHand
        if (!isBroken(item)) return item

        val reforger = MMOItemReforger(item)
        val readableName = item.itemMeta?.displayName()?.let { PLAIN.serialize(it) } ?: item.type.toString()

        if (!reforger.hasTemplate()) {
            return if (wildcard) null else throw RegenerateException("messages.item-not-reforgable", readableName)
        }

        if (!reforger.reforge(options)) {
            return if (wildcard) null else throw RegenerateException("messages.item-could-not-reforge", readableName)
        }

        val result = reforger.result
        player.inventory.setItemInMainHand(result)
        return result
    }
}