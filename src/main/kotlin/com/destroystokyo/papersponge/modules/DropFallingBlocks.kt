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

class DropFallingBlocks {

    /**
     * Block Y level at which we remove the entity and drop whatever the block was
     */
    private val maximumYLoc = 100 // TODO make configurable
    /**
     * Whether or not we should drop the item that the entity represents
     */
    private val shouldDropItem = true // TODO make configurable

    @Listener
    fun onEntityMove(event: MoveEntityEvent, @Getter("getTargetEntity") entity: Entity) {
        if (!isFallingBlockOrTNT(entity)) {
            return
        }

        if (!hasMovedBlockY(event.fromTransform, event.toTransform)) {
            return
        }

        if (event.toTransform.location.blockY >= maximumYLoc) {
            var item: ItemStack? = null

            if (shouldDropItem) {
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
