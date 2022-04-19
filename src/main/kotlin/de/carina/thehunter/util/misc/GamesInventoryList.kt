/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 19.04.22, 18:19 by Carina The Latest changes made by Carina on 19.04.22, 18:19 All contents of "GamesInventoryList.kt" are protected by copyright. The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */

package de.carina.thehunter.util.misc

import de.carina.thehunter.TheHunter
import de.carina.thehunter.util.builder.InventoryBuilder
import de.carina.thehunter.util.builder.ItemBuilder
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

class GamesInventoryList : Listener {

    companion object {
        fun createInventory(): Inventory {
            return InventoryBuilder(TheHunter.PREFIX + "§6Games", 54).addGamesToInventory().fillInventory(ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).addDisplayName("").addEnchantment(Enchantment.DURABILITY, 1).build()).create()
        }
    }

    @EventHandler
    fun onInventoryJoin(event: InventoryClickEvent) {
        if (PlainTextComponentSerializer.plainText().serialize(event.view.title()) != PlainTextComponentSerializer.plainText().serialize(LegacyComponentSerializer.legacySection().deserialize(TheHunter.PREFIX + "§6Games"))) {
            println("text2:" + PlainTextComponentSerializer.plainText().serialize(LegacyComponentSerializer.legacySection().deserialize(TheHunter.PREFIX + "§6Games")))
            println("text3:" + PlainTextComponentSerializer.plainText().serialize(event.view.title()))
            return
        }
        event.isCancelled = true
        if (event.currentItem == null)
            return
        if (event.currentItem!!.type != Material.ACACIA_SIGN)
            return
        val arenaName = PlainTextComponentSerializer.plainText().serialize(event.currentItem!!.itemMeta.displayName()!!)
        if (event.whoClicked !is Player)
            return
        (event.whoClicked as Player).performCommand("thehunter join " + arenaName)
    }
}