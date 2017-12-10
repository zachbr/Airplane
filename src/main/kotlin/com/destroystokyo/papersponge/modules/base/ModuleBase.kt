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

package com.destroystokyo.papersponge.modules.base

import com.destroystokyo.papersponge.PaperSponge
import org.spongepowered.api.Sponge

abstract class ModuleBase(moduleNameIn: String, instanceIn: PaperSponge) {
    private val moduleName = moduleNameIn
    protected val pluginInstance = instanceIn
    protected val logger = instanceIn.logger

    abstract fun shouldEnable(): Boolean

    fun register() {
        if (!shouldEnable()) {
            return
        }

        logger.info("Enabling " + moduleName)
        Sponge.getEventManager().registerListeners(pluginInstance, this)
    }
}
