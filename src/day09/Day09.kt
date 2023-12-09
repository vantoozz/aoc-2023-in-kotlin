package day09

import println
import readInput

fun main() {

    fun parseInput(input: List<String>) =
        input.map { line ->
            line.split(" ").map { it.toInt() }
        }

    tailrec fun expand(numbers: List<Int>, result: List<List<Int>> = emptyList()): List<List<Int>> {
        ((result.takeUnless { it.isEmpty() } ?: listOf(numbers))
                + listOf(numbers.zipWithNext { left, right -> right - left }))
            .let { expanded: List<List<Int>> ->
                return if (expanded.last().all { it == 0 }) expanded else expand(expanded.last(), expanded)
            }
    }

    fun part1(originalInput: List<String>) =
        parseInput(originalInput).sumOf {
            expand(it).reversed()
                .fold(0) { value: Int, list ->
                    list.last() + value
                }
        }

    fun part2(originalInput: List<String>) =
        parseInput(originalInput).sumOf {
            expand(it).reversed()
                .fold(0) { value: Int, list ->
                    list.first() - value
                }
        }

    check(part1(readInput("day09/test1")) == 114)
    check(part2(readInput("day09/test1")) == 2)

    val input = readInput("day09/Day09")

    part1(input).println()
    part2(input).println()
}
