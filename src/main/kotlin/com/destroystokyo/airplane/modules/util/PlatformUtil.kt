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

package com.destroystokyo.airplane.modules.util

import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.Transform
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*

/**
 * Gets an entity from the server via its a UUID
 */
fun getEntity(uuid: UUID): Optional<Entity> {
    val entity: Optional<Entity>? = Sponge.getGame().server.worlds.map { it.getEntity(uuid) }.firstOrNull { it.isPresent }
    return entity ?: Optional.empty()
}

/**
 * Gets whether the specified transforms represent a complete block level movement
 */
fun movedBlockXYZ(oldTransform: Transform<World>, newTransform: Transform<World>): Boolean {
    if (!oldTransform.isValid || !newTransform.isValid) {
        return false
    }

    return movedBlockXYZ(oldTransform.location, newTransform.location)
}

/**
 * Gets whether the specified locations represent a complete block level movement
 */
fun movedBlockXYZ(oldLoc: Location<World>, newLoc: Location<World>): Boolean {
    val oldX = oldLoc.blockX
    val oldY = oldLoc.blockY
    val oldZ = oldLoc.blockZ
    val newX = newLoc.blockX
    val newY = newLoc.blockY
    val newZ = newLoc.blockZ

    return oldX != newX || oldY != newY || oldZ != newZ
}

