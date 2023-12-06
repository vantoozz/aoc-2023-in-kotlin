package day05

import println
import readInput

fun main() {

    fun parseInput(input: List<String>): Input =
        Input(
            input.first()
                .substringAfter(":")
                .split(" ")
                .filterNot { it.isBlank() }
                .map {
                    it.toLong()
                }
                .toSet(),
            input.first()
                .substringAfter(":")
                .split(" ")
                .filterNot { it.isBlank() }
                .chunked(2)
                .map { (start, length) ->
                    start.toLong()..(start.toLong() + length.toLong())
                }
                .toSet(),
            input.subList(1, input.size)
                .filterNot { it.isBlank() }
                .fold(mutableListOf<MutableList<Input.RangeMap>>()) { acc, line ->
                    acc.also { ranges ->
                        when {
                            line.contains("map") -> {
                                ranges.add(mutableListOf())
                            }

                            else -> {
                                line.split(" ").map { it.toLong() }.let { numbers ->
                                    ranges.last().add(
                                        Input.RangeMap(
                                            destinationStart = numbers[0],
                                            sourceStart = numbers[1],
                                            length = numbers[2]
                                        )
                                    )

                                }
                            }
                        }
                    }
                }
        )

    fun process(input: Input, seed: Long) = input.rangeMaps.fold(seed) { source, rangeMaps ->
        rangeMaps.firstOrNull { rangeMap ->
            source in (rangeMap.sourceStart..(rangeMap.sourceStart + rangeMap.length))
        }?.let {
            it.destinationStart - it.sourceStart + source
        } ?: source
    }

    fun part1(rawInput: List<String>) =
        parseInput(rawInput).let { input ->
            input.seeds.minOf { seed ->
                process(input, seed)
            }
        }

    fun part2(rawInput: List<String>) =
        parseInput(rawInput).let { input ->
            sequence {
                input.seedsRanges
                    .forEach { range ->
                        yieldAll(range)
                    }
            }.map {
                process(input, it)
            }.min()
        }

    check(part1(readInput("day05/test1")) == 35L)
    check(part2(readInput("day05/test1")) == 46L)

    val input = readInput("day05/Day05")

    part1(input).println()
    part2(input).println()

}

data class Input(
    val seeds: Set<Long>,
    val seedsRanges: Set<LongRange>,
    val rangeMaps: List<List<RangeMap>>,
) {
    data class RangeMap(
        val destinationStart: Long,
        val sourceStart: Long,
        val length: Long,
    )
}
