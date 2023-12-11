package day10

import println
import readInput

fun main() {

    fun part1(originalInput: List<String>) =
        PipesMap.parse(originalInput).mainLoop.size / 2

    fun part2(originalInput: List<String>) =
        PipesMap.parse(originalInput).let { pipesMap ->
            pipesMap.map.foldIndexed(0) { y, result, row ->
                result + row.foldIndexed(0) foldRow@{ x, rowResult, tile ->
                    if (pipesMap.mainLoop.contains(tile)) {
                        return@foldRow rowResult
                    }

                    pipesMap.map[y].asSequence().take(x)
                        .filterIsInstance<MapObject.Pipe>()
                        .filter {
                            pipesMap.mainLoop.contains(it)
                        }
                        .map { it.type }
                        .count {
                            setOf(
                                MapObject.Pipe.Type.Vertical,
                                MapObject.Pipe.Type.NorthWest,
                                MapObject.Pipe.Type.NorthEast
                            ).contains(it)

                        }.takeIf {
                            it > 0 && it % 2 != 0
                        }?.let {
                            rowResult + 1
                        } ?: rowResult
                }
            }
        }

    check(part1(readInput("day10/test1")) == 4)
    check(part1(readInput("day10/test2")) == 4)
    check(part1(readInput("day10/test3")) == 8)
    check(part2(readInput("day10/test4")) == 4) { "Test 4 failed" }
    check(part2(readInput("day10/test5")) == 8) { "Test 5 failed" }
    check(part2(readInput("day10/test6")) == 10) { "Test 6 failed" }

    val input = readInput("day10/Day10")

    part1(input).println()
    part2(input).println()
}

data class PipesMap(
    val map: List<List<MapObject>>,
    val start: Location
) {

    init {
        require(map.all { it.size == map[0].size }) {
            "Map is not rectangular"
        }
        require(map.foldIndexed(true) { y, result, row ->
            result && row.foldIndexed(true) { x, consistent, mapObject ->
                consistent && mapObject.position == Location.Position(y, x)
            }
        }) { "Map is not consistent" }

    }

    val mainLoop: Set<MapObject.Pipe> by lazy {
        var currentPipe = startPipe()
        val visited = mutableSetOf<MapObject.Pipe>()
        do {
            val nextPipe = connections(currentPipe)
                .takeIf { it.size == 2 }
                ?.let { connections ->
                    connections.first { visited.lastOrNull() != it }
                } ?: error("Pipe has ${connections(currentPipe).size} connections")
            visited.add(currentPipe)
            currentPipe = nextPipe
        } while (nextPipe != startPipe())

        visited
    }

    fun neighbours(position: Location.Position): Set<MapObject> =
        sequence {
            (-1..1).forEach { dy ->
                (-1..1).forEach { dx ->
//                    if(kotlin.math.abs(dy) == kotlin.math.abs(dx)) return@forEach
                    map.getOrNull(position.y + dy)
                        ?.getOrNull(position.x + dx)
                        ?.let { yield(it) }
                }
            }
        }.filterNot { it.position == position }.toSet()

    fun connections(pipe: MapObject.Pipe) =
        neighbours(pipe.position)
            .mapNotNull {
                if (it is MapObject.Pipe) it else null
            }.filter {
                it.isConnectedTo(pipe)
            }.toSet()

    fun startPipe(): MapObject.Pipe = when (start) {
        is Location.Position ->
            if (map[start.y][start.x] is MapObject.Pipe) {
                map[start.y][start.x] as MapObject.Pipe
            } else error("Start position is not a pipe")

        else -> error("No start pipe found")
    }

    companion object {
        fun parse(input: List<String>) =
            input.foldIndexed(PipesMap(emptyList(), Location.Unknown)) { y, pipesMap, line ->
                line.foldIndexed(
                    Pair<List<MapObject>, Location>(
                        listOf<MapObject>(),
                        Location.Unknown
                    )
                ) { x, row, char ->
                    Location.Position(y, x).let { position ->
                        row.copy(
                            first =
                            row.first + listOf(when (char) {
                                '.', 'S' -> MapObject.Ground(position)
                                else -> {
                                    MapObject.Pipe.Type.entries
                                        .firstOrNull { it.sign == char }
                                        ?.let {
                                            MapObject.Pipe(it, position)
                                        } ?: error("Unknown map object type: $char")
                                }
                            }),
                            second = char.takeIf { it == 'S' }?.let {
                                check(row.second is Location.Unknown) {
                                    "Multiple start positions found"
                                }
                                position
                            } ?: row.second
                        )
                    }

                }.let { row ->
                    pipesMap.copy(
                        map = pipesMap.map + listOf(row.first),
                        start = row.second.takeIf { it is Location.Position }?.also {
                            check(pipesMap.start is Location.Unknown) {
                                "Multiple start positions found"
                            }
                        } ?: pipesMap.start
                    )
                }
            }.let { pipesMap ->
                check(pipesMap.start is Location.Position) {
                    "No start position found"
                }

                MapObject.Pipe.Type.entries.map {
                    MapObject.Pipe(it, pipesMap.start)
                }.firstOrNull { candidate ->
                    pipesMap.connections(candidate).size == 2
                }?.let { startPipe ->
                    pipesMap.copy(
                        map = pipesMap.map.foldIndexed(emptyList()) { y, result, row ->
                            result + listOf(
                                row.foldIndexed(emptyList()) { x, rowResult, mapObject ->
                                    rowResult + if (y == startPipe.position.y && x == startPipe.position.x) {
                                        startPipe
                                    } else {
                                        mapObject
                                    }
                                }
                            )
                        }
                    )
                } ?: pipesMap
            }

    }
}

sealed interface Location {
    data class Position(val y: Int, val x: Int) : Location

    data object Unknown : Location
}

sealed class MapObject(
    private val sign: Char,
    val position: Location.Position,
) {
    override fun toString(): String {
        return sign.toString() + position.toString()
    }

    class Ground(position: Location.Position) : MapObject('.', position)

    class Pipe(
        val type: Type,
        position: Location.Position
    ) : MapObject(type.sign, position) {

        fun isConnectedTo(other: Pipe) =
            Pair(
                other.position.y - position.y,
                other.position.x - position.x
            ).let { (dy, dx) ->
                when {
                    dy == -1 && dx == 0 -> Direction.North
                    dy == 1 && dx == 0 -> Direction.South
                    dy == 0 && dx == -1 -> Direction.West
                    dy == 0 && dx == 1 -> Direction.East
                    else -> null
                }?.let { direction ->
                    type.directions.contains(direction) &&
                            other.type.directions.contains(direction.opposite())
                } ?: false
            }

        enum class Type(val sign: Char, val directions: Set<Direction>) {
            Vertical('|', setOf(Direction.North, Direction.South)),
            Horizontal('-', setOf(Direction.East, Direction.West)),
            NorthEast('L', setOf(Direction.North, Direction.East)),
            NorthWest('J', setOf(Direction.North, Direction.West)),
            SouthEast('F', setOf(Direction.South, Direction.East)),
            SouthWest('7', setOf(Direction.South, Direction.West)),
        }
    }
}

enum class Direction {
    North,
    East,
    South,
    West;

    fun opposite(): Direction =
        when (this) {
            North -> South
            East -> West
            South -> North
            West -> East
        }
}
