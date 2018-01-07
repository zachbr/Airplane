# Airplane
This plugin ports a number of Paper-Server gameplay changes to Sponge, implemented as a Sponge-API plugin.

I am not sure how far I am going to take this. Currently I want to avoid mixins and stick to Sponge-API, though
that may change in the future. This was mostly an excuse to play with Sponge-API and kotlin.

## Modules
All changes are implemented as individual modules. Each module has its own global on/off switch and some
have their own configuration settings.

#### Aquatic Mob Spawns
Prevents water-based mobs from spawning in solid blocks

#### Drop Falling Blocks
Removes TNT and Falling Block entities that fly over a certain height, optionally drops them as items on removal.
Useful for nerfing long range TNT cannons on Factions servers

#### Nether Roof Damage
Makes the nether roof behave the same as the void beneath the world. Living entities are damaged over time, other
entities are removed immediately. Used to nerf long range nether travel in a vanilla-lore friendly way.
