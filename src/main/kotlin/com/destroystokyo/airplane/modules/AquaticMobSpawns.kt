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
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.Aquatic
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.SpawnEntityEvent
import java.util.function.Predicate

/**
 * The base game has a bad habit of occasionally spawning aquatic mobs
 * in solid blocks.
 *
 * Usually as a result of a mob spawner, but rarely as a natural spawn
 */
class AquaticMobSpawns(instance: Airplane) : ModuleBase("stricter-aquatic-mob-spawns", instance) {

    @Listener
    fun onMobSpawn(event: SpawnEntityEvent.Spawner) {
        event.filterEntities(entityCanSpawnHere)
    }

    /**
     * Checks if the entity can spawn
     * Given this module, we really only care about the Aquatic types
     *
     * If it's not an aquatic type it will always return true
     * If the aquatic type is in water it will return true
     */
    private val entityCanSpawnHere = Predicate { e: Entity ->
        if (e is Aquatic) {
            // If the entity is an aquatic type, make sure they're spawning in water, if not we need to return false
            val type = e.location.blockType
            return@Predicate type === BlockTypes.FLOWING_WATER || type === BlockTypes.WATER
        } else {
            // If they aren't, assume they can stay and return true
            return@Predicate true
        }
    }
}
