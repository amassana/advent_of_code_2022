package day3.a

import java.io.File
import java.io.InputStream
import kotlin.math.max

fun main() {
    val inputStream: InputStream = File("files/day3/input.txt").inputStream()
    var sum = 0;

    inputStream.bufferedReader().forEachLine {
        sum += calcPriority(spotRepeated(it))
    }
    println(sum)
}

private fun spotRepeated(it: String): Char {
    val letterSet1 = it.substring(0, it.length / 2).toSet()
    val letterSet2 = it.substring((it.length / 2)).toSet()

    return letterSet1.intersect(letterSet2).first()
}

private fun calcPriority(c: Char): Int {
    return when (c) {
        in 'a'..'z' -> c.code - 'a'.code + 1
        in 'A'..'Z' -> c.code - 'A'.code + 27
        else -> throw Exception ("Unexpected $c")
    }
}