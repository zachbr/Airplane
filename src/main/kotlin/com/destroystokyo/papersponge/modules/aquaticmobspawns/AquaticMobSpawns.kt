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

package com.destroystokyo.papersponge.modules.aquaticmobspawns

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
class AquaticMobSpawns {

    @Listener
    fun onMobSpawn(event: SpawnEntityEvent.Spawner) {
        event.filterEntities(aquaticInWater)
    }

    /**
     * Checks if the entity is an aquatic type out of water
     *
     * If it's not an aquatic type it will always return true
     * If the aquatic type is in water it will return true
     */
    private val aquaticInWater = Predicate { e: Entity ->
        if (e is Aquatic) {
            val type = e.location.blockType
            return@Predicate type === BlockTypes.FLOWING_WATER || type === BlockTypes.WATER
        } else {
            return@Predicate true
        }
    }
}
