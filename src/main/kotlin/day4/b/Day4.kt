package day4.b

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


    val (afrom1, ato1, afrom2, ato2) = "(\\d*)-(\\d*),(\\d*)-(\\d*)".toRegex().find(s)!!.destructured
    return from2 in from1..to1
            || from1 in from2..to2
}


private fun readElfInterval(s: String): Pair<Int, Int> {
    val elf = s.split('-')
    return elf[0].toInt() to elf[1].toInt()
}