/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 6/7/22, 3:37 AM by Carina The Latest changes made by Carina on 6/7/22, 3:36 AM All contents of "Ak.kt" are protected by copyright.
 * The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */
package de.carina.thehunter.guns

import de.carina.thehunter.TheHunter
import de.carina.thehunter.items.AmmoItems
import de.carina.thehunter.util.builder.ItemBuilder
import de.carina.thehunter.util.game.GamesHandler
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Ak : Gun {

    val shotBullets = mutableMapOf<Player, MutableSet<Arrow>>()
    private val reloading = mutableMapOf<Player, Boolean>()
    private val magazine = mutableMapOf<Player, Int>()
    val ak = ItemBuilder(Material.IRON_HOE).addDisplayName(TheHunter.prefix + "§7AK-47").addEnchantment(Enchantment.DURABILITY, 1).addLore("§7Right-click to shoot").build()
    private val bulletDelay = mutableMapOf<Player, Boolean>()

    private fun bulletDelayMaker(player: Player) {
        bulletDelay[player] = true
        Bukkit.getScheduler().runTaskLater(TheHunter.instance, Runnable {
            bulletDelay[player] = false
        }, 5L * GamesHandler.playerInGames[player]!!.gameItems.guns["ak-speed"] as Int)
    }

    private fun shootProjectile(player: Player) {

        bulletDelayMaker(player)
        val arrow = player.launchProjectile(Arrow::class.java, player.location.direction.multiply(GamesHandler.playerInGames[player]!!.gameItems.guns["ak-power"]!!))
        arrow.damage = 0.0
        player.world.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
        arrow.shooter = player
        magazine[player] = magazine[player]!! - 1
        if (shotBullets.containsKey(player)) {
            shotBullets[player]!!.add(arrow)
        } else {
            shotBullets[player] = mutableSetOf(arrow)
        }
    }


    override fun shoot(player: Player): Boolean {
        if (!reloading.containsKey(player)) {
            reloading[player] = false

        }
        if (!bulletDelay.containsKey(player))
            bulletDelay[player] = false

        if (!magazine.containsKey(player)) {
            magazine[player] = 0
            return false
        }
        if (magazine[player]!! <= 0) {
            player.sendMessage(TheHunter.instance.messages.messagesMap["gun-out-of-ammo"]!!)
            reloadGun(player)
            return false
        }

        if (!bulletDelay[player]!!) shootProjectile(player)

        return true
    }

    private fun checkAmmoPossible(player: Player): Boolean {
        if (!player.inventory.containsAtLeast(AmmoItems.akAmmo, 1)) {
            player.sendMessage(TheHunter.instance.messages.messagesMap["gun-out-of-ammo"]!!)
            return false
        }
        return true
    }

    fun reloadGun(player: Player) {
        player.playSound(player.location, Sound.BLOCK_ANVIL_LAND, 1f, 1f)
        if (reloading[player] == true) {
            player.sendMessage(TheHunter.instance.messages.messagesMap["gun-reloading"]!!)
            return
        }
        if (!checkAmmoPossible(player)) return
        player.sendMessage(TheHunter.instance.messages.messagesMap["gun-reloading"]!!)
        reloading[player] = true
        reload(player)
    }

    private fun reload(player: Player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(TheHunter.instance, {
            reloading[player] = false
            val amount = getAmmoAmount(player, AmmoItems.akAmmo)
            if (amount >= GamesHandler.playerInGames[player]!!.gameItems.guns["ak-ammo"]!!) magazine[player] = GamesHandler.playerInGames[player]!!.gameItems.guns["ak-ammo"]!!
            else magazine[player] = amount
            repeat(magazine[player]!!) {
                GunHandler.removeAmmo(player, Ak)
            }
            player.playSound(player.location, Sound.BLOCK_ANVIL_USE, 1f, 1f)
            player.sendMessage(TheHunter.instance.messages.messagesMap["gun-reload-done"]!!)
        }, 20L * GamesHandler.playerInGames[player]!!.gameItems.guns["ak-reload"]!!)
    }


    private fun getAmmoAmount(player: Player, ammo: ItemStack): Int {
        var amount = 0
        for (item in player.inventory.contents) {
            if (item == null) continue
            if (!item.hasItemMeta()) continue
            if (item.itemMeta == ammo.itemMeta) amount += item.amount
        }
        return amount
    }
}
