package day08

import println
import readInput

fun main() {

    fun greatestCommonDivisor(a: Long, b: Long): Long =
        if (b == 0L) a else greatestCommonDivisor(b, a % b)

    fun leastCommonMultiplier(a: Long, b: Long): Long =
        a / greatestCommonDivisor(a, b) * b

    fun parseInput(input: List<String>) =
        NodesMap(
            instructions = input.first().map { char ->
                when (char) {
                    'L' -> Instruction.LEFT
                    'R' -> Instruction.RIGHT
                    else -> error("Unknown instruction: $char")
                }
            },
            nodes = input.subList(2, input.size).associate { line ->
                line.split("= (", limit = 2).map { it.trim() }.let { parts ->
                    parts[1]
                        .trim('(', ')')
                        .split(",")
                        .map { it.trim() }.let {
                            Location(parts[0]) to Node(
                                left = Location(it[0]),
                                right = Location(it[1]),
                            )
                        }
                }
            }
        )

    fun pathLength(map: NodesMap, start: Location): Int {
        map.instructionsSequence.foldIndexed(start) { step, location, instruction ->
            if (location.isZ) {
                return step
            }
            map.nodes[location]!!.let { node ->
                when (instruction) {
                    Instruction.LEFT -> node.left
                    Instruction.RIGHT -> node.right
                }
            }
        }
        throw RuntimeException("Should not happen")
    }

    fun pathLength(map: NodesMap, starts: List<Location>): Long {
        return starts.fold(1L) { result, location ->
            leastCommonMultiplier(result, pathLength(map, location).toLong())
        }
    }

    fun part1(originalInput: List<String>) =
        pathLength(
            parseInput(originalInput),
            Location("AAA")
        )

    fun part2(originalInput: List<String>) =
        parseInput(originalInput).let { map ->
            map.nodes.keys
                .filter { it.isA }
                .take(6)
                .let {
                    pathLength(map, it)
                }
        }

    check(part1(readInput("day08/test1")) == 6)
    check(part1(readInput("day08/test2")) == 2)
    check(part2(readInput("day08/test3")) == 6L)

    val input = readInput("day08/Day08")

    part1(input).println()
    part2(input).println()
}

data class NodesMap(
    val instructions: List<Instruction>,
    val nodes: Map<Location, Node>
) {
    val instructionsSequence =
        sequence {
            while (true) {
                val iterator = instructions.iterator()
                while (iterator.hasNext()) {
                    yield(iterator.next())
                }
            }
        }
}

enum class Instruction {
    LEFT,
    RIGHT,
}

data class Location(
    val label: String
) {
    init {
        require(label.length == 3)
    }

    val isA by lazy {
        label.endsWith('A')
    }

    val isZ by lazy {
        label.endsWith('Z')
    }

    override fun toString() = label
}

data class Node(
    val left: Location,
    val right: Location,
)
