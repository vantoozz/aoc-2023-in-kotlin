package day06

import println
import readInput
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {

    fun String.parse(extraFn: String.() -> String = { this }) =
        substringAfter(":").extraFn()
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.toLong() }

    fun parseInput(input: List<String>, extraFn: String.() -> String = { this }): List<Result> =
        input[0].parse(extraFn).zip(input[1].parse(extraFn)).map {
            Result(it.first, it.second)
        }

    fun process(input: List<Result>) = input.map { result ->
        realRoots(-1, result.time, -result.distance)
            ?.let {
                Pair(floor(it.first).toLong(), ceil(it.second).toLong())
            }?.let {
                (it.second - it.first - 1) +
                        if (result.isBetter(it.first)) 1 else 0 +
                                if (result.isBetter(it.first)) 1 else 0
            }?.toInt() ?: 0

    }.fold(1) { acc, i ->
        acc * i
    }

    fun part1(originalInput: List<String>) =
        process(parseInput(originalInput))

    fun part2(originalInput: List<String>) =
        process(parseInput(originalInput) { this.replace(" ", "") })

    check(part1(readInput("day06/test1")) == 288)
    check(part2(readInput("day06/test1")) == 71503)

    val input = readInput("day06/Day06")

    part1(input).println()
    part2(input).println()

}

class Result(
    val time: Long,
    val distance: Long,
) {
    fun isBetter(speed: Long) =
        (speed * (time - speed)) > distance
}

fun realRoots(a: Long, b: Long, c: Long) =
    (b * b - 4 * a * c)
        .takeIf { it >= 0 }
        ?.toDouble()?.let { d ->
            Pair(
                (-b + sqrt(d)) / (2 * a),
                (-b - sqrt(d)) / (2 * a)
            )
        }
