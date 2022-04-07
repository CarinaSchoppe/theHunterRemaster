/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 07.04.22, 20:18 by Carina The Latest changes made by Carina on 07.04.22, 20:18 All contents of "Countdown.kt" are protected by copyright. The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */

package de.carina.thehunter.countdowns

import de.carina.thehunter.util.game.Game

abstract class Countdown(val game: Game) {

    abstract var duration: Int
    abstract fun idle()
    abstract fun start()
    abstract fun stop()
    abstract var isIdle: Boolean

}