fun main() {

    val digits = mapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
    )

    fun part1(input: List<String>): Int =
        input.sumOf { line ->
            "${line.first { it.isDigit() }}${line.last { it.isDigit() }}".toInt()
        }

    fun part2(input: List<String>): Int =
        input.map { line ->
            line.foldIndexed(StringBuilder()) { offset, decoded, char ->
                decoded.apply {
                    char.takeIf { it.isDigit() }?.let { append(it) }
                        ?: run {
                            digits.forEach { (word, digit) ->
                                if (line.startsWith(word, offset)) {
                                    append(digit)
                                }
                            }
                        }
                }
            }.toString()
        }.let {
            part1(it)
        }

    check(part1(readInput("Day01_test")) == 142)
    check(part2(readInput("Day01_test2")) == 281)
    check(part2(readInput("Day01_test3")) == 68)

    val input = readInput("Day01")

    part1(input).println()
    part2(input).println()
}
