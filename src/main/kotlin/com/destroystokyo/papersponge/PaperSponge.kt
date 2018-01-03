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

import com.destroystokyo.papersponge.config.Configuration
import com.destroystokyo.papersponge.modules.AquaticMobSpawns
import com.destroystokyo.papersponge.modules.DropFallingBlocks
import com.destroystokyo.papersponge.modules.NetherRoofDamage
import com.destroystokyo.papersponge.modules.base.ModuleBase
import com.google.inject.Inject
import org.slf4j.Logger
import org.spongepowered.api.Game
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStoppedServerEvent
import org.spongepowered.api.plugin.Plugin
import java.nio.file.Path

@Plugin(id = "papersponge", name = "PaperSponge", url = "https://github.com/zachbr/PaperSponge")
class PaperSponge {

    @Inject internal lateinit var logger: Logger
    @Inject private lateinit var game: Game

    @Inject
    @DefaultConfig(sharedRoot = true)
    private var configPath: Path? = null // This is apparently not injected in time for a lateinit assignment, which sucks

    /**
     * Collection of all active modules
     */
    private val activeModules: ArrayList<ModuleBase> = ArrayList()

    /**
     * PaperSponge's configuration manager for use throughout modules and the plugin
     */
    internal var configManager: Configuration? = null

    @Listener
    fun onServerStart(event: GameStartedServerEvent) {
        logger.info("Enabling PaperSponge")

        // Because we cannot lateinit the configPath, we have to assign the config manager here in server start
        // which means elvis operators all over the place :(
        configManager = Configuration(this, configPath)
        configManager?.readConfig()

        initializeModules()
    }

    @Listener
    fun onServerStop(event: GameStoppedServerEvent) {
        logger.info("Disabling PaperSponge")
        activeModules.forEach { it.disable() }
        configManager?.saveConfig()
    }

    @Listener
    fun onReloadRequest(event: GameReloadEvent) {
        logger.info("Reloading PaperSponge")
        disableAllModules()
        configManager?.readConfig()
        initializeModules()
    }

    /**
     * Initializes all modules
     *
     * Each module will handle its own registration, including whether or not it's actually enabled
     */
    private fun initializeModules() {
        val modules: ArrayList<ModuleBase> = ArrayList()
        modules.add(AquaticMobSpawns(this))
        modules.add(DropFallingBlocks(this))
        modules.add(NetherRoofDamage(this))

        for (module in modules) {
            module.initialize()

            if (module.enabled) {
                activeModules.add(module)
            }
        }

        configManager?.saveConfig() // update config with module options
    }

    private fun disableAllModules() {
        activeModules.forEach { it.disable() }
        activeModules.clear()
    }

}
