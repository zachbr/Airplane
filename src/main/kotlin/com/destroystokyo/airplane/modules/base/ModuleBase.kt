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

package com.destroystokyo.airplane.modules.base

import com.destroystokyo.airplane.Airplane
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import org.apache.commons.lang3.Validate
import org.spongepowered.api.Sponge

abstract class ModuleBase(moduleNameIn: String, instanceIn: Airplane) {

    /**
     * Module name - Should be entirely lowercase and use dashes in place of spaces
     *
     * For example: "stricter-aquatic-mob-spawns"
     * This is enforced because we use this module name for configuration
     */
    internal val moduleName = moduleNameIn.replace(' ', '-').toLowerCase()

    /**
     * Instance of the main plugin class for use by modules
     */
    protected val pluginInstance = instanceIn

    /**
     * Instance of the main plugin logger
     */
    protected val logger = instanceIn.logger

    /**
     * Represents whether this module is enabled
     * Only an enabled module actually does anything
     */
    internal var enabled: Boolean = false

    /**
     * Gets whether or not this module should enable
     */
    private fun shouldEnable(): Boolean {
        Validate.notNull(pluginInstance.configManager)
        val enableNode = getTopLevelModuleConfigNode().getNode("enabled")

        if (enableNode.isVirtual) {
            enableNode.setComment("Controls the global on/off state of the module")
            enableNode.value = true
        }

        return enableNode.boolean
    }

    /**
     * Initializes the module
     *
     * It will only be enabled if set so in the config
     */
    internal fun initialize() {
        if (!shouldEnable()) {
            return
        }

        logger.info("Enabling module " + moduleName)
        enabled = true
        Sponge.getEventManager().registerListeners(pluginInstance, this)
        onModuleEnable()
    }

    /**
     * Disables the module
     */
    internal fun disable() {
        logger.info("Disabling module " + moduleName)
        Sponge.getEventManager().unregisterListeners(this)
        onModuleDisable()
        enabled = false
    }

    /**
     * Gets the top level of this module's config node
     *
     * We want the ModuleBase class to have exclusive access here so that we
     * can ensure visibility of priority nodes, ex: the enable node
     */
    private fun getTopLevelModuleConfigNode(): CommentedConfigurationNode {
        return pluginInstance.configManager!!.getNode("modules", moduleName) // assert as we checked in module initialize
    }

    /**
     * Gets the module's configuration node
     */
    protected fun getModuleConfigNode(): CommentedConfigurationNode {
        return getTopLevelModuleConfigNode().getNode("values")
    }

    /**
     * Called after the module is registered and enabled
     */
    protected open fun onModuleEnable() {}

    /**
     * Called before the module is disabled
     */
    protected open fun onModuleDisable() {}
}
