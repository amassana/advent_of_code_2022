package day4.a

import java.io.File
import java.io.InputStream

fun main() {
    val inputStream: InputStream = File("files/day4/input.txt").inputStream()
    var count = 0

    inputStream.bufferedReader().forEachLine {
        if (findOverLap(it))
            count++
    }

    println(count)
}

private fun findOverLap(s: String): Boolean {
    val parts = s.split(',')
    val (from1, to1) = readElfInterval(parts[0])
    val (from2, to2) = readElfInterval(parts[1])
    return from1 >= from2 && to1 <= to2
            || from2 >= from1 && to2 <= to1
}

private fun readElfInterval(s: String): Pair<Int, Int> {
    val elf = s.split('-')
    return elf[0].toInt() to elf[1].toInt()
}