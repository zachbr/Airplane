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

package com.destroystokyo.papersponge.config

import com.destroystokyo.papersponge.PaperSponge
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.apache.commons.lang3.Validate
import java.io.IOException
import java.nio.file.Path
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.HeaderMode


class Configuration(pluginIn: PaperSponge, pathIn: Path) {

    /**
     * The config file for use by PaperSponge
     */
    private val configPath = pathIn

    /**
     * An instance of the main plugin class
     */
    private val pluginInstance = pluginIn

    /**
     * Our configuration loader
     */
    private var configurationLoader: HoconConfigurationLoader? = null

    /**
     * Root configuration node
     */
    private var rootNode: SimpleCommentedConfigurationNode? = null

    /**
     * Reads PaperSponge's shared configuration file
     * Must be called on enable, before modules can start reading from their nodes
     */
    internal fun readConfig(): Boolean {
        configurationLoader = HoconConfigurationLoader.builder()
                .setPath(configPath)
                .setHeaderMode(HeaderMode.PRESET)
                .build()

        try {
            rootNode = SimpleCommentedConfigurationNode.root(ConfigurationOptions.defaults().setHeader(getHeader()))
        } catch (ex: IOException) {
            pluginInstance.logger.error("Could not load PaperSponge configuration!")
            ex.printStackTrace()
            return false
        }

        return true
    }

    internal fun saveConfig(): Boolean {
        Validate.notNull(configurationLoader)
        Validate.notNull(rootNode)

        try {
            configurationLoader?.save(rootNode)
        } catch (ex: IOException) {
            pluginInstance.logger.error("Could not save PaperSponge configuration!")
            ex.printStackTrace()
            return false
        }

        return true
    }


    /**
     * Gets a child node from the root node
     */
    internal fun getNode(vararg keys: String): CommentedConfigurationNode {
        Validate.notNull(rootNode)

        return rootNode!!.getNode(*keys) // assert as we just checked it, this must use the spread operator
    }

    /**
     * Gets the super serious and entirely professional header for the config
     */
    private fun getHeader(): String {
        return "______  ___  ______ ___________  ___________ _____ _   _ _____  _____\n" +
                "| ___ \\/ _ \\ | ___ \\  ___| ___ \\/  ___| ___ \\  _  | \\ | |  __ \\|  ___|\n" +
                "| |_/ / /_\\ \\| |_/ / |__ | |_/ /\\ `--.| |_/ / | | |  \\| | |  \\/| |__\n" +
                "|  __/|  _  ||  __/|  __||    /  `--. \\  __/| | | | . ` | | __ |  __|\n" +
                "| |   | | | || |   | |___| |\\ \\ /\\__/ / |   \\ \\_/ / |\\  | |_\\ \\| |___\n" +
                "\\_|   \\_| |_/\\_|   \\____/\\_| \\_|\\____/\\_|    \\___/\\_| \\_/\\____/\\____/"
    }
}
