/*
 * This file is part of SpongePaper.
 *
 * SpongePaper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpongePaper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SpongePaper.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.destroystokyo.spongepaper

import com.google.inject.Inject
import org.slf4j.Logger
import org.spongepowered.api.Game
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

@Plugin(id = "spongepaper", name = "SpongePaper", url = "https://github.com/zachbr/SpongePaper")
class SpongePaper {

    @Inject private lateinit var logger: Logger
    @Inject private lateinit var game: Game

    @Listener
    fun onServerStart(event: GameStartedServerEvent) {
        // Hey! The server has started!
        this.logger.info("Hello world!")
        // Try loading some configuration settings for a welcome message to players
        // when they join!
    }


    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join, @Getter("getTargetEntity") player: Player) {
        // The text message could be configurable, check the docs on how to do so!
        player.sendMessage(Text.of(TextColors.AQUA, TextStyles.BOLD, "Hi " + player.name))
    }

}
