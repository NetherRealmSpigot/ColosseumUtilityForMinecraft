package colosseum.utility

import colosseum.utility.UtilMath.trim
import com.google.common.collect.Lists
import kotlin.math.max
import kotlin.math.min
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldBorder
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector
import java.util.function.Function

object UtilWorld {
    @JvmStatic
    fun getWorld(world: String): World {
        return Bukkit.getServer().getWorld(world)
    }

    @JvmStatic
    fun isInChunk(location: Location, chunk: Chunk): Boolean {
        return location.chunk.x == chunk.x && location.chunk.z == chunk.z && chunk.world == location.chunk.world
    }

    @JvmStatic
    fun areChunksEqual(first: Location, second: Location): Boolean {
        return (first.blockX shr 4) == (second.blockX shr 4) && (first.blockZ shr 4) == (second.blockZ shr 4)
    }

    @JvmStatic
    fun chunkToStr(chunk: Chunk?): String {
        return if (chunk == null) "" else chunkToStr(chunk.world.name, chunk.x, chunk.z)
    }

    @JvmStatic
    fun chunkToStr(location: Location): String {
        return chunkToStr(location.world.name, location.blockX shr 4, location.blockZ shr 4)
    }

    @JvmStatic
    fun chunkToStr(world: String, x: Int, z: Int): String {
        return "$world,$x,$z"
    }

    @JvmStatic
    fun chunkToStrClean(chunk: Chunk?): String {
        return if (chunk == null) "" else "(${chunk.x},${chunk.z})"
    }

    @JvmStatic
    fun strToChunk(string: String): Chunk? {
        try {
            val tokens = string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return getWorld(tokens[0]).getChunkAt(tokens[1].toInt(), tokens[2].toInt())
        } catch (e: Exception) {
            return null
        }
    }

    @JvmStatic
    fun blockToStr(block: Block?): String {
        return if (block == null) "" else "${block.world.name},${block.x},${block.y},${block.z}"
    }

    @JvmStatic
    fun blockToStrClean(block: Block?): String {
        return if (block == null) "" else "(${block.x},${block.y},${block.z})"
    }

    @JvmStatic
    fun strToBlock(string: String): Block? {
        if (string.isEmpty()) {
            return null
        }

        val parts = string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {
            for (cur in Bukkit.getServer().worlds) {
                if (cur.name.equals(parts[0], ignoreCase = true)) {
                    val x = parts[1].toInt()
                    val y = parts[2].toInt()
                    val z = parts[3].toInt()
                    return cur.getBlockAt(x, y, z)
                }
            }
        } catch (e: Exception) {
            // NO-OP
        }
        return null
    }

    @JvmStatic
    fun locToStr(loc: Location?): String {
        return if (loc == null) "" else "${loc.world.name},${trim(1, loc.x)},${trim(1, loc.y)},${trim(1, loc.z)}"
    }

    @JvmStatic
    fun locToStrClean(loc: Location?): String {
        return if (loc == null) "Null" else "(${loc.blockX},${loc.blockY},${loc.blockZ})"
    }

    @JvmStatic
    fun vecToStrClean(loc: Vector?): String {
        return if (loc == null) "Null" else "(${loc.x},${loc.y},${loc.z})"
    }

    @JvmStatic
    fun strToLoc(string: String): Location? {
        if (string.isEmpty()) {
            return null
        }

        val tokens = string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {
            for (cur in Bukkit.getServer().worlds) {
                if (cur.name.equals(tokens[0], ignoreCase = true)) {
                    return Location(cur, tokens[1].toDouble(), tokens[2].toDouble(), tokens[3].toDouble())
                }
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    @JvmStatic
    fun locMerge(a: Location, b: Location): Location {
        a.x = b.x
        a.y = b.y
        a.z = b.z
        return a
    }

    @JvmStatic
    fun envToStr(env: World.Environment): String {
        return when (env) {
            World.Environment.NORMAL -> "Overworld"
            World.Environment.NETHER -> "Nether"
            World.Environment.THE_END -> "The End"
            else -> "Unknown"
        }
    }

    @JvmStatic
    fun getWorldType(env: World.Environment): World? {
        for (cur in Bukkit.getServer().worlds) {
            if (cur.environment == env) {
                return cur
            }
        }
        return null
    }

    @JvmStatic
    fun averageLocation(locs: Collection<Location>): Location? {
        if (locs.isEmpty()) {
            return null
        }

        val vec = Vector(0, 0, 0)
        var count = 0.0

        var world: World? = null

        for (spawn in locs) {
            count++
            vec.add(spawn.toVector())
            world = spawn.world
        }

        vec.multiply(1.0 / count)

        return vec.toLocation(world)
    }

    @JvmStatic
    fun branch(origin: Location): List<Block> {
        return Lists.newArrayList(
            origin.block,
            origin.block.getRelative(BlockFace.DOWN),
            origin.block.getRelative(BlockFace.UP),
            origin.block.getRelative(BlockFace.NORTH),
            origin.block.getRelative(BlockFace.EAST),
            origin.block.getRelative(BlockFace.SOUTH),
            origin.block.getRelative(BlockFace.WEST)
        )
    }

    @JvmStatic
    var getBorderSize: Function<WorldBorder, Double> = (Function { border: WorldBorder -> border.size / 2 })
    @JvmStatic
    var getBorderMaxX: Function<WorldBorder, Double> = (Function { border: WorldBorder -> getBorderSize.apply(border) })
    @JvmStatic
    var getBorderMaxZ: Function<WorldBorder, Double> = (Function { border: WorldBorder -> getBorderSize.apply(border) })
    @JvmStatic
    var getBorderMinX: Function<WorldBorder, Double> = (Function { border: WorldBorder -> -getBorderSize.apply(border) })
    @JvmStatic
    var getBorderMinZ: Function<WorldBorder, Double> = (Function { border: WorldBorder -> -getBorderSize.apply(border) })

    /**
     * This method will use the World provided by the given Location.
     *
     *
     *
     * @return **true** if the specified location is within the bounds of the
     * world's set border, or **false** if [World.getWorldBorder] returns null.
     */
    @JvmStatic
    fun inWorldBorder(location: Location): Boolean {
        return inWorldBorder(location.world, location)
    }

    /**
     * This method will use the World specified by the second argument, and the
     * x, y, and z provided by the given Location.
     *
     *
     *
     * @return **true** if the specified location is within the bounds of the
     * world's set border, or **false** if [World.getWorldBorder] returns null.
     */
    @JvmStatic
    fun inWorldBorder(world: World, location: Location): Boolean {
        val border = world.worldBorder ?: return false

        val borderSize = getBorderSize.apply(border)

        val maxX = borderSize
        val maxZ = borderSize
        val minX = -borderSize
        val minZ = -borderSize

        return location.x in minX..maxX && location.z in minZ..maxZ
    }

    /**
     * @return **true** if the specified bounding box is within the bounds of the
     * world's set border, or **false** if [World.getWorldBorder] returns null.
     */
    @JvmStatic
    fun isBoxInWorldBorder(world: World, min: Location, max: Location): Boolean {
        val border = world.worldBorder ?: return false

        val borderSize = getBorderSize.apply(border)

        val maxX = borderSize
        val maxZ = borderSize
        val minX = -borderSize
        val minZ = -borderSize

        val startX = min(min.x, max.x)
        val startZ = min(min.z, max.z)
        val endX = max(min.x, max.x)
        val endZ = max(min.z, max.z)

        return startX >= minX && startZ <= maxX && endX >= minZ && endZ <= maxZ
    }
}
