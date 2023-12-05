package day04

import println
import readInput
import kotlin.math.pow

fun main() {

    fun part1(input: List<String>) =
        input
            .map { Card.fromString(it) }
            .sumOf { it.worth() }

    fun part2(input: List<String>) =
        input
            .map { Card.fromString(it).power() }
            .foldIndexed(IntArray(input.size)) { index, deck, power ->
                deck.also {
                    deck[index] += 1
                    (index + 1..index + power).forEach {
                        deck[it] += deck[index]
                    }
                }
            }.sum()


    check(part1(readInput("day04/test1")) == 13)
    check(part2(readInput("day04/test1")) == 30)

    val input = readInput("day04/Day04")

    part1(input).println()
    part2(input).println()

}


data class Card(
    val winningNumbers: List<Int>,
    val numbers: List<Int>,
) {
    fun worth() =
        2.0.pow(power() - 1).toInt()

    fun power() =
        winningNumbers.intersect(numbers.toSet()).size

    companion object {
        fun fromString(line: String) =
            line.substringAfter(":")
                .split("|", limit = 2)
                .let {
                    Card(
                        it[0].extractNumbers(),
                        it[1].extractNumbers(),
                    )
                }
    }
}


fun String.extractNumbers() =
    split(" ").filter { it.isNotBlank() }.map { it.toInt() }
