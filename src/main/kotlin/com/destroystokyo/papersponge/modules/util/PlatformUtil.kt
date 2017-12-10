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

package com.destroystokyo.papersponge.modules.util

import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.Entity
import java.util.*

/**
 * Gets an entity from the server via its a UUID
 */
fun getEntity(uuid: UUID): Optional<Entity> {
    val entity: Optional<Entity>? = Sponge.getGame().server.worlds.map { it.getEntity(uuid) }.firstOrNull { it.isPresent }
    return entity ?: Optional.empty()
}

