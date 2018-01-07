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

package com.destroystokyo.airplane.modules

import com.destroystokyo.airplane.Airplane
import com.destroystokyo.airplane.modules.base.ModuleBase
import ninja.leaping.configurate.ConfigurationNode
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.FallingBlock
import org.spongepowered.api.entity.Transform
import org.spongepowered.api.entity.explosive.PrimedTNT
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.World

/**
 * Removes falling blocks and primed TNT entities from the server once they
 * reach the specified height.
 *
 * Commonly used in TNT cannon servers to nerf certain types of cannons
 */
class DropFallingBlocks(instanceIn: Airplane) : ModuleBase("drop-falling-blocks-and-tnt", instanceIn) {

    private var maxTNTHeight: Int = -1
    private var maxFallingBlockHeight: Int = -1
    private var shouldDropTNT: Boolean = false
    private var shouldDropFallingBlock: Boolean = false

    override fun onModuleEnable() {
        val moduleNode = getModuleConfigNode()
        val tntHeightNode = moduleNode.getNode("maximum", "tnt-height")
        val fallingBlockHeightNode = moduleNode.getNode("maximum","falling-block-height")
        val tntDropNode = moduleNode.getNode("drop", "tnt-items")
        val fallingDropNode = moduleNode.getNode("drop", "falling-block-items")

        if (tntHeightNode.isVirtual) {
            val comment = String.format("The height at which a TNT entity should be removed%n" +
                    "Set to 0 to disable")
            tntHeightNode.setComment(comment)
            tntHeightNode.value = 256
        }

        if (fallingBlockHeightNode.isVirtual) {
            val comment = String.format("The height at which a falling block should be removed%n" +
                    "Set to 0 to disable")
            fallingBlockHeightNode.setComment(comment)
            fallingBlockHeightNode.value = 256
        }

        if (tntDropNode.isVirtual) {
            val comment = "Whether or not we should drop a new TNT item when we remove it"
            tntDropNode.setComment(comment)
            tntDropNode.value = true
        }

        if (fallingDropNode.isVirtual) {
            val comment = String.format("Whether or not we should drop a new item of the falling block's type%n" +
                    " when we remove it")
            fallingDropNode.setComment(comment)
            fallingDropNode.value = true
        }

        maxTNTHeight = tntHeightNode.int
        maxFallingBlockHeight = fallingBlockHeightNode.int
        shouldDropTNT = tntDropNode.boolean
        shouldDropFallingBlock = fallingDropNode.boolean

        if (isInvalidHeight(tntHeightNode)) { maxTNTHeight = 256 }
        if (isInvalidHeight(fallingBlockHeightNode)) { maxFallingBlockHeight = 256 }
    }

    @Listener
    fun onEntityMove(event: MoveEntityEvent, @Getter("getTargetEntity") entity: Entity) {
        if (!isFallingBlockOrTNT(entity)) {
            return
        }

        if (!hasMovedBlockY(event.fromTransform, event.toTransform)) {
            return
        }

        if (shouldRemove(entity)) {
            val itemToDrop = getItemToDrop(entity)

            if (itemToDrop != null) {
                val loc = entity.location
                val dropEntity = loc.extent.createEntity(EntityTypes.ITEM, loc.position)
                dropEntity.offer(Keys.REPRESENTED_ITEM, itemToDrop.createSnapshot())
                loc.extent.spawnEntity(dropEntity)
            }

            entity.remove()
        }
    }

    /**
     * Checks if the given transforms have different block Y levels
     */
    private fun hasMovedBlockY(oldTransform: Transform<World>, newTransform: Transform<World>): Boolean {
        if (!oldTransform.isValid || !newTransform.isValid) {
            return false
        }

        val oldY = oldTransform.location.blockY
        val newY = newTransform.location.blockY

        return oldY != newY
    }

    /**
     * Checks that the given entity is an instance of either PrimedTNT or FallingBlock
     */
    private fun isFallingBlockOrTNT(entity: Entity): Boolean = entity is FallingBlock || entity is PrimedTNT

    /**
     * Checks whether or not we should remove the entity
     */
    private fun shouldRemove(entity: Entity): Boolean {
        when (entity) {
            is PrimedTNT -> {
                if (maxTNTHeight != 0 && entity.location.blockY > maxTNTHeight) {
                    return true
                }
            }

            is FallingBlock -> {
                if (maxFallingBlockHeight != 0 && entity.location.blockY > maxFallingBlockHeight) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Gets the item to drop
     *
     * Will return null if there is no item to drop
     */
    private fun getItemToDrop(entity: Entity): ItemStack? {
        when (entity) {
            is PrimedTNT -> {
                if (shouldDropTNT) {
                    return ItemStack.of(ItemTypes.TNT, 1)
                }
            }

            is FallingBlock -> {
                if (shouldDropFallingBlock) {
                    val opt = entity.blockState().get().type.item
                    if (opt.isPresent) {
                        return ItemStack.of(opt.get(), 1)
                    }
                }
            }
        }

        return null
    }

    /**
     * Checks that the given node is equal to zero or greater than zero
     *
     * If the node is not greater than or equal to zero, it will print out a warning
     * and return true
     *
     * If the node is greater than or equal to zero, it will return false
     */
    private fun isInvalidHeight(node: ConfigurationNode): Boolean {
        if (node.int < 0) {
            logger.warn(moduleName + " - " + node.string + " is too low!")
            logger.warn(moduleName + " - It must be greater than or equal to 0! Using 256 instead.")
            return true
        } else {
            return false
        }
    }
}
