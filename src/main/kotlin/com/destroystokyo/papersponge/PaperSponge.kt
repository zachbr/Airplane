/*
 * This file is part of PaperSponge.
 *
 * PaperSponge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaperSponge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PaperSponge.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.destroystokyo.papersponge

import com.destroystokyo.papersponge.modules.AquaticMobSpawns
import com.destroystokyo.papersponge.modules.NetherRoofDamage
import com.google.inject.Inject
import org.slf4j.Logger
import org.spongepowered.api.Game
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.plugin.Plugin

@Plugin(id = "papersponge", name = "PaperSponge", url = "https://github.com/zachbr/PaperSponge")
class PaperSponge {

    @Inject private lateinit var logger: Logger
    @Inject private lateinit var game: Game

    @Listener
    fun onServerStart(event: GameStartedServerEvent) {
        logger.info("Enabling PaperSponge")
        registerListeners()
    }

    @Listener
    fun onReloadRequest(event: GameReloadEvent) {
        game.eventManager.unregisterListeners(this)
        logger.info("Reloading PaperSponge")
        registerListeners()
    }

    /**
     * Registers all of this plugin's listeners
     * // TODO Make config driven
     */
    private fun registerListeners() {
        game.eventManager.registerListeners(this, AquaticMobSpawns())
        game.eventManager.registerListeners(this, NetherRoofDamage(this))
    }

}
