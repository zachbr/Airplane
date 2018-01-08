/*
 * This file is part of Airplane.
 *
 * Airplane is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Airplane is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Airplane.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.destroystokyo.airplane

import com.destroystokyo.airplane.config.Configuration
import com.destroystokyo.airplane.modules.AquaticMobSpawns
import com.destroystokyo.airplane.modules.DropFallingBlocks
import com.destroystokyo.airplane.modules.NetherRoofDamage
import com.destroystokyo.airplane.modules.base.ModuleBase
import com.google.inject.Inject
import org.apache.commons.lang3.Validate
import org.slf4j.Logger
import org.spongepowered.api.Game
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStoppedServerEvent
import org.spongepowered.api.plugin.Plugin
import java.nio.file.Path

@Plugin(id = "airplane", name = "Airplane", url = "https://github.com/zachbr/Airplane")
class Airplane {

    @Inject internal lateinit var logger: Logger
    @Inject private lateinit var game: Game

    @Inject
    @DefaultConfig(sharedRoot = true)
    private var configPath: Path? = null // This is apparently not injected in time for a lateinit assignment, which sucks

    /**
     * Collection of all active modules
     */
    private val activeModules = ArrayList<ModuleBase>()

    /**
     * Airplane's configuration manager for use throughout modules and the plugin
     */
    internal var configManager: Configuration? = null

    @Listener
    fun onServerStart(event: GameStartedServerEvent) {
        logger.info("Enabling Airplane")

        // Because we cannot lateinit the configPath, we have to assign the config manager here in server start
        // which means elvis operators all over the place :(
        Validate.notNull(configPath, "Configuration path has still not been injected yet!!!")
        configManager = Configuration(this, configPath!!) // assert, we just checked and I don't want configManager accepting null paths
        configManager?.readConfig()

        initializeModules()
    }

    @Listener
    fun onServerStop(event: GameStoppedServerEvent) {
        logger.info("Disabling Airplane")
        disableAllModules()
    }

    @Listener
    fun onReloadRequest(event: GameReloadEvent) {
        logger.info("Reloading Airplane")
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
        val modules = ArrayList<ModuleBase>()
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
