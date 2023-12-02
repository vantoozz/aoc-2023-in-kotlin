package day02

import println
import readInput
import kotlin.math.max
import kotlin.math.min

fun main() {

    fun parseLine(line: String): Pair<Int, List<Take>> =
        line.split(":", limit = 2)
            .let { parts ->
                parts[0].split(" ").last().toInt() to
                        parts[1].split(";").map {
                            Take.fromString(it)
                        }
            }

    fun part1(input: List<String>): Int =
        input.map { parseLine(it) }
            .sumOf { (id, takes) ->
                try {
                    takes.forEach {
                        it.validate()
                    }
                    id
                } catch (e: Exception) {
                    0
                }
            }

    fun part2(input: List<String>): Int =
        input.map { parseLine(it) }
            .map { it.second }
            .sumOf { takes ->
                takes.fold(Triple(1,1,1)) { maximals, b: Take ->
                    Triple(
                        max(maximals.first, b.red),
                        max(maximals.second, b.green),
                        max(maximals.third, b.blue),
                    )
                }.let {
                    it.first * it.second * it.third
                }
            }

    check(part1(readInput("day02/test1")) == 8)
    check(part2(readInput("day02/test1")) == 2286)

    val input = readInput("day02/Day02")

    part1(input).println()
    part2(input).println()
}

data class Take(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    fun validate() {
        require(red in 0..12)
        require(green in 0..13)
        require(blue in 0..14)
    }

    companion object {
        fun fromString(string: String): Take =
            string.split(",")
                .map {
                    it.trim().split(" ")
                        .let { cubes -> cubes[1] to cubes[0].toInt() }
                }
                .fold(Take(0, 0, 0)) { take, cubes ->
                    when (cubes.first) {
                        "red" -> take.copy(red = take.red + cubes.second)
                        "green" -> take.copy(green = take.green + cubes.second)
                        "blue" -> take.copy(blue = take.blue + cubes.second)
                        else -> take
                    }
                }
    }
}
