package day2.b

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
    val matchOutcome = MatchOutcome.from(match[2])
    val myPlay = rivalPlay.neededTo(matchOutcome)

    return myPlay.value() + matchOutcome.value()
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

    fun neededTo(matchOutcome: MatchOutcome): Play {
        if (matchOutcome == MatchOutcome.DRAW) return this
        return when {
            this == ROCK && matchOutcome == MatchOutcome.WIN -> PAPER
            this == ROCK && matchOutcome == MatchOutcome.LOSE -> SCISSORS
            this == PAPER && matchOutcome == MatchOutcome.WIN -> SCISSORS
            this == PAPER && matchOutcome == MatchOutcome.LOSE -> ROCK
            this == SCISSORS && matchOutcome == MatchOutcome.WIN -> ROCK
            this == SCISSORS && matchOutcome == MatchOutcome.LOSE -> PAPER
            else -> throw Exception("Bad programmer")
        }
    }

    companion object {
        fun from(c: Char) : Play {
            return when (c) {
                'A' -> ROCK;
                'B' -> PAPER;
                'C' -> SCISSORS;
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

    companion object {
        fun from(c: Char) : MatchOutcome {
            return when (c) {
                'X' -> LOSE
                'Y' -> DRAW
                'Z' -> WIN
                else -> throw Exception("UNKNOWN '$c'")
            }
        }
    }
}
