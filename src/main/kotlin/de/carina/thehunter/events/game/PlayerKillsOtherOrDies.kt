/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 20.04.22, 10:48 by Carina The Latest changes made by Carina on 20.04.22, 10:48 All contents of "PlayerKillsOtherOrDies.kt" are protected by copyright. The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */

package de.carina.thehunter.events.game

import de.carina.thehunter.TheHunter
import de.carina.thehunter.gamestates.IngameState
import de.carina.thehunter.items.configurator.LeaveItem
import de.carina.thehunter.util.game.Game
import de.carina.thehunter.util.game.GamesHandler
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerKillsOtherOrDies : Listener {

    @EventHandler
    fun onPlayerKillsOther(event: PlayerDeathEvent) {
        if (event.entity.killer != null) {
            if (event.entity.killer!! !is Player) {
                addDeathToPlayer(event, event.player)
            } else {
                playerKilledOther(event, event.player, event.entity.killer!!)
            }
        } else {
            addDeathToPlayer(event, event.player)
        }
    }

    private fun playerKilledOther(event: PlayerDeathEvent, player: Player, killer: Player) {
        if (!GamesHandler.playerInGames.containsKey(player))
            return
        if (!GamesHandler.playerInGames.containsKey(killer))
            return
        if (GamesHandler.playerInGames[player] != GamesHandler.playerInGames[killer])
            return
        val game = GamesHandler.playerInGames[player]!!
        if (game.currentGameState !is IngameState)
            return
        TheHunter.instance.statsSystem.playerKilledOtherPlayer(killer, player)
        generalHandling(player, game)
        event.deathMessage(Component.text(""))
        playerHiding(player, game, killer)
        player.sendMessage(TheHunter.instance.messages.messagesMap["player-own-killed-by-other"]!!.replace("%killer%", killer.name))
        if (game.checkWinning())
            game.nextGameState()
    }

    private fun playerHiding(player: Player, game: Game, killer: Player) {
        game.players.forEach {
            it.sendMessage(TheHunter.instance.messages.messagesMap["player-killed-by-other"]!!.replace("%player%", player.name).replace("%killer%", killer.name))
            it.hidePlayer(TheHunter.instance, player)
            player.hidePlayer(TheHunter.instance, it)
        }
        game.spectators.filter { it.name != player.name }.forEach {
            it.sendMessage(TheHunter.instance.messages.messagesMap["player-killed-by-other"]!!.replace("%player%", player.name).replace("%killer%", killer.name))
            it.hidePlayer(TheHunter.instance, player)
            player.hidePlayer(TheHunter.instance, it)
        }
    }

    private fun addDeathToPlayer(event: PlayerDeathEvent, player: Player) {
        if (!GamesHandler.playerInGames.containsKey(player))
            return
        val game = GamesHandler.playerInGames[player]!!
        if (game.currentGameState !is IngameState)
            return
        TheHunter.instance.statsSystem.playerDied(player)
        generalHandling(player, game)
        event.deathMessage(Component.text(""))
        game.players.forEach {
            it.sendMessage(TheHunter.instance.messages.messagesMap["player-died"]!!.replace("%player%", player.name))

        }
        game.spectators.filter { it.name != player.name }.forEach {
            it.sendMessage(TheHunter.instance.messages.messagesMap["player-died"]!!.replace("%player%", player.name))

        }
        player.sendMessage(TheHunter.instance.messages.messagesMap["player-own-died"]!!)
        if (game.checkWinning())
            game.nextGameState()

    }

    private fun generalHandling(player: Player, game: Game) {
        game.deathChests.createDeathChest(player)
        game.players.remove(player)
        game.spectators.add(player)
        player.inventory.setItem(8, LeaveItem.createLeaveItem())
        player.teleport(game.spectatorLocation!!)
        player.inventory.clear()

        GamesHandler.playerInGames.keys.forEach {
            it.hidePlayer(TheHunter.instance, player)
        }
        GamesHandler.spectatorInGames.keys.forEach {
            it.hidePlayer(TheHunter.instance, player)
            player.hidePlayer(TheHunter.instance, it)
        }
    }
}