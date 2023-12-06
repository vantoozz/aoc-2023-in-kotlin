package day06

import println
import readInput

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
        (0..result.time).map { speed ->
            speed * (result.time - speed)
        }.count { it > result.distance }
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
)
