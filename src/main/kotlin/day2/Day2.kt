package day2

import java.io.File
import java.io.InputStream
import kotlin.math.max

fun main() {
    val inputStream: InputStream = File("files/day2/input1.txt").inputStream()
    var counter = 0

    inputStream.bufferedReader()
        .forEachLine {
            counter += process(it)
        }
    println(counter)
}

fun process(match: String): Int {

    val rivalPlay = Play.from(match[0])
    val myPlay = Play.from(match[2])

    return myPlay.value() + myPlay.matchOutcome(rivalPlay).value()
}

enum class Play {
    ROCK,
    PAPER,
    SCISSORS;

    fun value() : Int {
        return when (this) {
            ROCK -> 1
            PAPER -> 2
            SCISSORS -> 3
        }
    }

    fun matchOutcome(other: Play) : MatchOutcome {
        if (other == this) return MatchOutcome.DRAW
        return when {
            this == ROCK && other == SCISSORS -> MatchOutcome.WIN
            this == ROCK && other == PAPER -> MatchOutcome.LOSE
            this == PAPER && other == ROCK -> MatchOutcome.WIN
            this == PAPER && other == SCISSORS -> MatchOutcome.LOSE
            this == SCISSORS && other == PAPER -> MatchOutcome.WIN
            this == SCISSORS && other == ROCK -> MatchOutcome.LOSE
            else -> throw Exception("Bad programmer")
        }
    }

    companion object {
        fun from(c: Char) : Play {
            return when (c) {
                'A' -> ROCK;
                'B' -> PAPER;
                'C' -> SCISSORS;
                'X' -> ROCK;
                'Y' -> PAPER;
                'Z' -> SCISSORS
                else -> throw Exception("UNKNOWN '$c'")
            }
        }
    }
}

enum class MatchOutcome {
    WIN,DRAW,LOSE;
    fun value() : Int {
        return when (this) {
            WIN -> 6
            DRAW -> 3
            LOSE -> 0
        }
    }
}
