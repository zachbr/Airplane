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
import com.destroystokyo.airplane.modules.util.getEntity
import com.destroystokyo.airplane.modules.util.movedBlockXYZ
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.entity.damage.DamageTypes
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.world.DimensionTypes
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

/**
 * Sometimes players (and other entities) get on top of the nether's roof
 *
 * This will damage them as if they were in the void below the map
 */
class NetherRoofDamage(instance: Airplane) : ModuleBase("nether-roof-damager", instance) {

    /**
     * Damage source to use for all entity damage calls
     */
    private val damageSource = DamageSource.builder().type(DamageTypes.VOID).creative().magical().build()

    /**
     * List of entity UUIDs actively on the nether roof
     */
    private val entitiesOnNetherRoof = HashSet<UUID>()

    /**
     * List of entity UUIDs that are awaiting addition to the main list
     */
    private val awaitingAdd = HashSet<UUID>()

    override fun onModuleEnable() {
        submitRunnable(pluginInstance)
    }

    @Listener
    fun onEntitySpawn(event: SpawnEntityEvent, @Getter("getEntities") entities: List<Entity>) {
        entities.filter { shouldDamage(it.location) }.mapTo(awaitingAdd) { it.uniqueId }
    }

    @Listener
    fun onEntityMove(event: MoveEntityEvent, @Getter("getTargetEntity") entity: Entity) {
        if (!movedBlockXYZ(event.fromTransform, event.toTransform)) {
            return
        }

        val loc = event.toTransform.location
        if (shouldDamage(loc)) {
            awaitingAdd.add(entity.uniqueId)
        }
    }

    /**
     * Damages the specified entity as if it was in the void
     *
     * For living entities this means a small amount of damage dealt this cycle
     * For other entities this means complete removal
     */
    private fun damage(entity: Entity) {
        if (entity is Living) {
            entity.damage(2.5, damageSource)
        } else {
            entity.remove()
        }
    }

    /**
     * Gets whether we should damage the entity at this location
     */
    private fun shouldDamage(loc: Location<World>): Boolean {
        if (loc.extent.dimension.type !== DimensionTypes.NETHER) {
            return false
        }

        return loc.y >= 128
    }

    /**
     * Creates and submits our damage runnable
     */
    private fun submitRunnable(instance: Airplane) {
        val damageRunnable = java.lang.Runnable {
            entitiesOnNetherRoof += awaitingAdd
            awaitingAdd.clear()

            val iterator = entitiesOnNetherRoof.iterator()
            while (iterator.hasNext()) {
                val uuid = iterator.next()
                val opt = getEntity(uuid)

                if (!opt.isPresent) {
                    iterator.remove()
                    continue
                }

                val entity = opt.get()
                if (shouldDamage(entity.location)) {
                    damage(entity)
                } else {
                    iterator.remove()
                }
            }
        }

        Task.builder()
                .interval(1, TimeUnit.SECONDS)
                .execute(damageRunnable)
                .name("Airplane - Nether Roof Damage Task")
                .submit(instance)
    }
}
