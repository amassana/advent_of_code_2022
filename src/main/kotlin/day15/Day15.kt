package day15

import java.io.File
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    val data = File("files/day15/input.txt").inputStream().bufferedReader().lineSequence()
        .map { Reading.of(it) }
        .toList()

    part2(data, 4_000_000)

    //part1(data, 2000000)
}

fun part2(data: List<Reading>, max: Int) {
    println("${LocalDateTime.now()}")

    for (line in 0..max) {
        val ranges =  lineCoverage(data, line)
        if (ranges.size == 1 && ranges[0].first < 0 && ranges[0].last > max) // _the_ time saver: can't be a candidate, skip
            continue
        for (x in 0..max) {
            if (ranges.none { x in it } ) {
                println("$line $x ${x*4000000L + line}") // the response ^^'
                println("${LocalDateTime.now()}")
                return
            }
        }
        println("This is never printed")
    }
}

// (list of) ranges that are covered by the sensors in the given line
fun lineCoverage(data: List<Reading>, line: Int): List<IntRange> {
    var ranges = data.map { reading ->
        val range = reading.rangeInLine(line)
        if (range == IntRange.EMPTY) {
            return@map null
        }
        return@map range
    }
        .filterNotNull()

    do {
        val prev = ranges.size
        ranges = merge(ranges)
    } while (prev != ranges.size)

    return ranges
}

fun part1(data: List<Reading>, line: Int) {
    val ranges = lineCoverage(data, line)

    val occupiedPositions =
        (data.map { it.beaconX to it.beaconY } + data.map { it.sensorX to it.sensorY }).distinct()

    val occupied = occupiedPositions.sumOf { (x, y) ->
        if (y == line && ranges.any { x in it })
            return@sumOf 1
        return@sumOf 0 as Int
    }

    val discarded = (ranges
        .sumOf{ it.length })

    val final = discarded - occupied

    println(final)
}

/*
    Tries to simplify the list of ranges by finding out ranges that overlap and merging them
 */
private fun merge(ranges: List<IntRange>): List<IntRange> {
    // find a candidate, merge and return. Otherwise, search for another candidate.
    for (range in ranges) {
        var newRange = range // candidate
        var newList = mutableListOf<IntRange>() // new ranges list if we could merge
        for (test in ranges) {
            if (newRange == test) // skip myself or equivalents to me
                continue
            if (newRange.canBeMerged(test)) { // merge the ranges
                newRange = newRange.merge(test)
            }
            else { // the tested range can't be merged. push to the list
                newList += test
            }
        }
        if (newRange == range) // no change was made. Let's try another candidate.
            continue

        newList.add(newRange) // changes were mede. Add the larger built range and return

        return newList
    }

    return ranges
}

private fun IntRange.merge(test: IntRange): IntRange {
    val newFirst = min(this.first, test.first)
    val newLast = max(this.last, test.last)
    return newFirst..newLast
}

private fun IntRange.canBeMerged(test: IntRange) =
    test.first in this || test.last in this || this.first in test || this.last in test

private val IntRange.length: Int
    get() { return this.last - this.first + 1 }

class Reading(val sensorX: Int, val sensorY: Int, val beaconX: Int, val beaconY: Int) {
    private val distance = abs(sensorX - beaconX) + abs(sensorY - beaconY)

    /*
        Returns the range of scanned positions by this sensor-beacon pair. Might be EMPTY
     */
    fun rangeInLine(y: Int): IntRange {
        val yDistance = abs(y - sensorY)
        if (yDistance > distance)
            return IntRange.EMPTY
        val a = sensorX - (distance - yDistance)
        val b = sensorX + (distance - yDistance)
        return (a..b)
    }

    fun contains(x: Int, y: Int): Boolean = abs(sensorY - y) + abs(sensorX - x) <= distance

    override fun toString(): String {
        return "Sensor($sensorX, $sensorY) Beacon($beaconX, $beaconY)"
    }

    companion object {
        fun of(s: String): Reading {/*
            val (sensorX, sensorY, beaconX, beaconY) =
                "Sensor at x=(\\d*), y=(\\d*): closest beacon is at x=(\\d*), y=(\\d*)"
                    .toRegex().find(s)!!.destructured
            */
            val sensorX = s.removePrefix("Sensor at x=").substringBefore(",").toInt()
            var s2 = s.substringAfter(",")
            val sensorY = s2.removePrefix(" y=").substringBefore(":").toInt()
            s2 = s2.substringAfter("at x=")
            val beaconX = s2.substringBefore(",").toInt()
            val beaconY = s2.substringAfterLast("=").toInt()
            return Reading(sensorX, sensorY, beaconX, beaconY)
        }
    }
}
