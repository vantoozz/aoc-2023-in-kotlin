package day07

import println
import readInput

fun main() {

    fun parseInput(input: List<String>, j: Card) =
        input
            .map { it.split(" ", limit = 2) }
            .map { (hand, bid) ->
                Pair(Hand(hand.map {
                    when (it) {
                        'A' -> Card.ACE
                        'K' -> Card.KING
                        'Q' -> Card.QUEEN
                        'J' -> j
                        'T' -> Card.TEN
                        '9' -> Card.NINE
                        '8' -> Card.EIGHT
                        '7' -> Card.SEVEN
                        '6' -> Card.SIX
                        '5' -> Card.FIVE
                        '4' -> Card.FOUR
                        '3' -> Card.THREE
                        '2' -> Card.TWO
                        else -> throw IllegalArgumentException("Unknown card $it")
                    }
                }), bid.toInt())
            }

    fun process(originalInput: List<String>, j: Card) =
        parseInput(originalInput, j)
            .sortedBy { it.first }
            .map { it.second }
            .mapIndexed { index, bid -> (index + 1) * bid }
            .sum()

    fun part1(originalInput: List<String>) =
        process(originalInput, Card.JACK)

    fun part2(originalInput: List<String>) =
        process(originalInput, Card.JOKER)

    check(part1(readInput("day07/test1")) == 6440)
    check(part2(readInput("day07/test1")) == 5905)

    val input = readInput("day07/Day07")

    part1(input).println()
    part2(input).println()
}

class Hand(private val cards: List<Card>) : Comparable<Hand> {

    init {
        require(cards.size == 5)
    }

    override fun toString(): String =
        cards.joinToString(", ") { it.name }

    private val type: Type by lazy {
        when {
            isFiveOfAKind() -> Type.FIVE_OF_A_KIND
            isFourOfAKind() -> Type.FOUR_OF_A_KIND
            isFullHouse() -> Type.FULL_HOUSE
            isThreeOfAKind() -> Type.THREE_OF_A_KIND
            isTwoPairs() -> Type.TWO_PAIRS
            isOnePair() -> Type.ONE_PAIR
            else -> Type.HIGH_CARD
        }
    }

    private val jokersCount =
        cards.count { it == Card.JOKER }

    private fun isFiveOfAKind(): Boolean =
        cards
            .groupBy { it }
            .values
            .any { it.size + jokersCount == 5 } || jokersCount == 5

    private fun isFourOfAKind(): Boolean =
        cards
            .groupBy { it }
            .values
            .any { it.first() != Card.JOKER && it.size + jokersCount == 4 }
                || jokersCount == 4

    private fun isFullHouse(): Boolean =
        cards
            .groupBy { it }
            .values
            .let { values ->
                (values.filter { it.size == 2 }.size == 2 && jokersCount == 1)
                        || (values.any { it.size == 3 } && values.any { it.size == 2 })
            }

    private fun isThreeOfAKind(): Boolean =
        cards
            .groupBy { it }
            .values
            .any { it.size + jokersCount == 3 } || jokersCount == 3

    private fun isTwoPairs(): Boolean =
        cards
            .groupBy { it }
            .values
            .count { it.size == 2 } == 2

    private fun isOnePair(): Boolean =
        cards
            .groupBy { it }
            .values
            .any { it.size + jokersCount == 2 }

    override fun compareTo(other: Hand): Int {
        val typeComparison = type.compareTo(other.type)
        if (typeComparison != 0) {
            return typeComparison
        }

        return cards
            .zip(other.cards)
            .map { (card, otherCard) ->
                card.compareTo(otherCard)
            }
            .firstOrNull { it != 0 }
            ?: 0
    }
}

enum class Type {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIRS,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND,
}

enum class Card {
    JOKER,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE,
}
