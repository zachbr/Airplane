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

package com.destroystokyo.papersponge.modules

import com.destroystokyo.papersponge.PaperSponge
import com.destroystokyo.papersponge.modules.base.ModuleBase
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
class DropFallingBlocks(instanceIn: PaperSponge) : ModuleBase("Falling Block Killer", instanceIn) {

    private var maximumYLoc: Int = -1
    private var shouldDropItem: Boolean = false

    override fun onModuleEnable() {
        val moduleNode = getModuleConfigNode()
        val heightNode = moduleNode.getNode("maximum-height")
        val dropNode = moduleNode.getNode("drop-item")

        if (heightNode.isVirtual) {
            heightNode.value = 256
        }

        if (dropNode.isVirtual) {
            dropNode.value = true
        }

        maximumYLoc = heightNode.int
        shouldDropItem = dropNode.boolean

        if (maximumYLoc < 0) {
            logger.warn(moduleName + " - maximum-height in config is too low!")
            logger.warn(moduleName + " - It must be greater than 0! Using 256 instead.")
            maximumYLoc = 256
        }
    }

    @Listener
    fun onEntityMove(event: MoveEntityEvent, @Getter("getTargetEntity") entity: Entity) {
        if (!isFallingBlockOrTNT(entity)) {
            return
        }

        if (!hasMovedBlockY(event.fromTransform, event.toTransform)) {
            return
        }

        if (event.toTransform.location.blockY >= maximumYLoc) {
            if (shouldDropItem) {
                var item: ItemStack? = null

                if (entity is FallingBlock) {
                    val opt = entity.blockState().get().type.item
                    if (opt.isPresent) {
                        item = ItemStack.of(opt.get(), 1)
                    }
                } else {
                    item = ItemStack.of(ItemTypes.TNT, 1)
                }

                if (item != null) {
                    val loc = entity.location
                    val dropEntity = loc.extent.createEntity(EntityTypes.ITEM, loc.position)
                    dropEntity.offer(Keys.REPRESENTED_ITEM, item.createSnapshot())
                    loc.extent.spawnEntity(dropEntity)
                }
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
}
