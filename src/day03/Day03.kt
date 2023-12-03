package day03

import println
import readInput

fun main() {

    fun List<String>.expanded(): List<String> =
        first().length.let { width ->
            listOf(".".repeat(width + 2)) + map { ".$it." } + listOf(".".repeat(width + 2))
        }

    fun parseLine(line: String): Map<Int, Int> =
        line.foldIndexed(mutableMapOf()) { position, numbers, char ->
            numbers.also {
                char.takeIf { it.isDigit() }?.toString()?.toInt()?.let { digit ->
                    if (line[position - 1].isDigit()) {
                        numbers.keys.last().let {
                            numbers[it] = (numbers[it] ?: 0) * 10 + digit
                        }
                    } else {
                        numbers[position] = digit
                    }
                }
            }
        }

    fun part1(originalInput: List<String>): Int =
        originalInput.expanded().let { input ->
            input.map { parseLine(it) }
                .foldIndexed(0) { y, sum, line ->
                    sum + line.filter { (x, number) ->
                        (y - 1..y + 1).any { iy ->
                            (x - 1..x + number.toString().length).any { ix ->
                                input[iy][ix].let { char ->
                                    char != '.' && !char.isDigit()

                                }
                            }
                        }
                    }.values.sum()
                }
        }

    fun part2(originalInput: List<String>): Int =
        originalInput.expanded().let { input ->
            input.map { parseLine(it) }.let { numbers: List<Map<Int, Int>> ->
                input.foldIndexed(0) { y, sum, line ->
                    sum + line.foldIndexed(0) { x, lineSum, char ->
                        lineSum + (char.takeIf { it == '*' }?.let {
                            numbers.slice(y - 1..y + 1)
                                .foldIndexed(mutableListOf<Int>()) { ny, connected, lineNumbers ->
                                    connected.also {
                                        lineNumbers
                                            .filter { (nx, number) ->
                                                x in nx - 1..nx + number.toString().length
                                            }
                                            .forEach { (nx, number) ->
                                                (-1..1).any { iy ->
                                                    (nx - 1..nx + number.toString().length).any { ix ->
                                                        input[iy + y][ix] == '*'
                                                    }
                                                }.takeIf { it }?.let {
                                                    connected.add(number)
                                                }
                                            }
                                    }
                                }
                                .takeIf {
                                    it.size == 2
                                }
                                ?.let { it[0] * it[1] }
                        } ?: 0)
                    }
                }
            }
        }

    check(part1(readInput("day03/test1")) == 4361)
    check(part2(readInput("day03/test1")) == 467835)

    val input = readInput("day03/Day03")

    part1(input).println()
    part2(input).println()
}
