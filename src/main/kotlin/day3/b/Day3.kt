package day3.b

import java.io.File
import java.io.InputStream
import kotlin.math.max

fun main() {
    val inputStream: InputStream = File("files/day3/input.txt").inputStream()
    var sum = inputStream.bufferedReader().readLines()
        .chunked(3)
        .map {
            val set1 = it[0].toSet()
            val set2 = it[1].toSet()
            val set3 = it[2].toSet()
            set1.intersect(set2).intersect(set3).first()
        }
        .sumOf { calcPriority(it) }

    println(sum)
}


private fun calcPriority(c: Char): Int {
    return when (c) {
        in 'a'..'z' -> c.code - 'a'.code + 1
        in 'A'..'Z' -> c.code - 'A'.code + 27
        else -> throw Exception ("Unexpected $c")
    }
}