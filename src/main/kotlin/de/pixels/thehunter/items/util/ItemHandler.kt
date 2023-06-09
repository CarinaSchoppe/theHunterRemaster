/*
 * Copyright Notice for theHunterRemaster Copyright (c) at Carina Sophie Schoppe 2022 File created on 9/26/22, 11:08 PM by Carina Sophie The Latest changes made by Carina Sophie on 8/30/22, 10:45 PM All contents of "ItemHandler.kt" are protected by copyright. The copyright law, unless expressly indicated otherwise, is at Carina Sophie Schoppe. All rights reserved Any type of duplication, distribution, rental, sale, award, Public accessibility or other use requires the express written consent of Carina Sophie Schoppe.
 */

package de.pixels.thehunter.items.util

import de.pixels.thehunter.gamestates.IngameState
import de.pixels.thehunter.util.game.management.GamesHandler
import de.pixels.thehunter.util.misc.Permissions
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object ItemHandler {

    fun shouldNotInteractWithItem(event: PlayerInteractEvent, item: ItemStack, itemString: String): Boolean {

        if (event.item == null || !event.player.hasPermission("${Permissions.PERMISSION_PREFIX}.$itemString") || event.item?.hasItemMeta() == false || event.item?.itemMeta != item.itemMeta || !event.player.inventory.itemInMainHand.hasItemMeta() || event.player.inventory.itemInMainHand.itemMeta != item.itemMeta || !GamesHandler.playerInGames.containsKey(event.player) || !event.action.isRightClick || GamesHandler.playerInGames[event.player]?.currentGameState !is IngameState || GamesHandler.playerInGames[event.player]?.gameItems?.items?.get(
                itemString
            ) == false
        )
            return true
        event.isCancelled = true
        return false
    }

    fun removeOneItemOfPlayer(player: Player) {
        val item = player.inventory.itemInMainHand
        if (item.amount == 1) {
            player.inventory.setItemInMainHand(null)
        } else {
            item.amount -= 1
            player.inventory.setItemInMainHand(item)
        }

    }
}
