package day11

import println
import readInput
import kotlin.math.max
import kotlin.math.min

fun main() {

    fun allPathsLength(space: Space) =
        space.galaxies.foldIndexed(listOf<Long>()) { offset, result, galaxyA ->
            result + space.galaxies.subList(offset, space.galaxies.size).fold(listOf()) { distances, galaxyB ->
                distances + galaxyA.pathLength(galaxyB)
            }
        }.let {
            it.sumOf {
                it
            }
        }

    fun part1(originalInput: List<String>) =
        allPathsLength(Space.parse(originalInput, 2))

    fun part2(originalInput: List<String>, ageFactor: Long) =
        allPathsLength(Space.parse(originalInput, ageFactor))

    check(part1(readInput("day11/test1")) == 374L)
    check(part2(readInput("day11/test1"), 10) == 1030L)
    check(part2(readInput("day11/test1"), 100) == 8410L)

    val input = readInput("day11/Day11")

    part1(input).println()
    part2(input, 10000000).println()
}

class Space(
    val map: List<List<MapObject>>,
    private val ageFactor: Long,
) {
    init {
        require(map.all { it.size == map[0].size }) {
            "Map is not rectangular"
        }
    }

    val costY: List<Long> by lazy {
        List(map.size) { y ->
            if (galaxies.none { it.y == y }) ageFactor else 1
        }
    }

    val costX: List<Long> by lazy {
        List(map.first().size) { x ->
            if (galaxies.none { it.x == x }) ageFactor else 1
        }
    }

    inner class Location(val y: Int, val x: Int) {

        fun pathLength(other: Location): Long =
            (
                    (min(y, other.y)..<max(y, other.y)).fold(0L) { sum: Long, y ->
                        sum + costY[y]
                    }
                    ) + (
                    (min(x, other.x)..<max(x, other.x)).fold(0L) { sum: Long, x ->
                        sum + costX[x]
                    }
                    )

    }

    override fun toString() =
        map.joinToString("\n") { row ->
            row.joinToString("") { it.sign.toString() }
        }

    val galaxies: List<Location> by lazy {
        map.foldIndexed(mutableListOf()) { y, galaxies, row ->
            galaxies.apply {
                row.forEachIndexed { x, tile ->
                    if (tile == MapObject.Galaxy) {
                        add(Location(y, x))
                    }
                }
            }
        }
    }

    companion object {
        fun parse(input: List<String>, ageFactor: Long) = Space(
            input.map { row ->
                row.map { char ->
                    MapObject.entries.find { it.sign == char }
                        ?: error("Unknown map object: $char")
                }
            }, ageFactor
        )

    }
}

enum class MapObject(val sign: Char) {
    Empty('.'),
    Galaxy('#'),
}
