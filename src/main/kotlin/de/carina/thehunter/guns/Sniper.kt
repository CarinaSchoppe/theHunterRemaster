/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 6/7/22, 3:33 AM by Carina The Latest changes made by Carina on 6/7/22, 3:33 AM All contents of "Sniper.kt" are protected by copyright.
 * The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */
package de.carina.thehunter.guns

import de.carina.thehunter.TheHunter
import de.carina.thehunter.events.game.PlayerHotbarHover
import de.carina.thehunter.items.AmmoItems
import de.carina.thehunter.util.builder.ItemBuilder
import de.carina.thehunter.util.game.GamesHandler
import de.carina.thehunter.util.misc.ConstantStrings
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Sniper : Gun {

    private val bulletDelay = mutableMapOf<Player, Boolean>()

    val shotBullets = mutableMapOf<Player, MutableSet<Arrow>>()
    private val reloading = mutableMapOf<Player, Boolean>()
    val magazine = mutableMapOf<Player, Int>()
    val sniper =
        ItemBuilder(Material.DIAMOND_HOE).addDisplayName(TheHunter.prefix + "§7Sniper").addEnchantment(Enchantment.DURABILITY, 1).addLore("§7Right-click to shoot").build()

    private fun bulletDelayMaker(player: Player) {
        bulletDelay[player] = true
        Bukkit.getScheduler().runTaskLater(TheHunter.instance, Runnable {
            bulletDelay[player] = false
        }, 5L * GamesHandler.playerInGames[player]!!.gameItems.guns["sniper-speed"] as Int)
    }

    private fun shootProjectile(player: Player) {
        bulletDelayMaker(player)
        val arrow = player.launchProjectile(
            Arrow::class.java, player.location.direction.multiply(
                GamesHandler.playerInGames[player]!!.gameItems.guns["sniper-power"]!!
            )
        )
        arrow.damage = 0.0
        player.playSound(player, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1f)
        player.world.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
        magazine[player] = magazine[player]!! - 1
        arrow.shooter = player
        PlayerHotbarHover.updateHotbar(sniper, player)

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
            reloadGun(player)
            return false
        }

        if (!bulletDelay[player]!!) shootProjectile(player)
        return true
    }

    private fun checkAmmoPossible(player: Player): Boolean {
        if (!player.inventory.containsAtLeast(AmmoItems.sniperAmmo, 1)) {
            player.sendMessage(TheHunter.instance.messages.messagesMap["gun-out-of-ammo"]!!)
            return false
        }

        return true
    }

    fun reloadGun(player: Player) {
        if (magazine.getOrDefault(player, 0) >= GamesHandler.playerInGames[player]!!.gameItems.guns[ConstantStrings.SNIPER_AMMO]!!)
            return
        player.playSound(player.location, Sound.BLOCK_ANVIL_LAND, 1f, 1f)
        if (reloading[player] == true) {
            player.sendMessage(TheHunter.instance.messages.messagesMap["gun-reloading"]!!)
            return
        }
        if (!checkAmmoPossible(player))
            return
        player.sendMessage(TheHunter.instance.messages.messagesMap["gun-reloading"]!!)
        reloading[player] = true
        reload(player)
    }

    private fun reload(player: Player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(TheHunter.instance, {
            reloading[player] = false
            val amount = getAmmoAmount(player, AmmoItems.sniperAmmo)
            val old = magazine.getOrDefault(player, 0)
            if (amount + old >= GamesHandler.playerInGames[player]!!.gameItems.guns[ConstantStrings.SNIPER_AMMO]!!) {
                magazine[player] = GamesHandler.playerInGames[player]!!.gameItems.guns[ConstantStrings.SNIPER_AMMO]!!
            } else
                magazine[player] = amount + old
            repeat(magazine[player]!! - old) {
                GunHandler.removeAmmo(player, Sniper)
            }
            PlayerHotbarHover.updateHotbar(sniper, player)

            player.playSound(player.location, Sound.BLOCK_ANVIL_USE, 1f, 1f)

            player.sendMessage(TheHunter.instance.messages.messagesMap["gun-reload-done"]!!)
        }, 20L * GamesHandler.playerInGames[player]!!.gameItems.guns["sniper-reload"]!!)
    }


    private fun getAmmoAmount(player: Player, ammo: ItemStack): Int {
        var amount = 0
        for (item in player.inventory.contents) {
            if (item == null)
                continue
            if (!item.hasItemMeta())
                continue
            if (item.itemMeta == ammo.itemMeta)
                amount += item.amount
        }
        return amount
    }
}
