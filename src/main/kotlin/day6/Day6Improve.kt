package day6

import java.io.File
import java.io.InputStream


fun main() {
    val inputStream: InputStream = File("files/day6/example1.txt").inputStream()

    val line = inputStream.bufferedReader().readLine()

    val markerSize = 4

    val result = line.windowedSequence(markerSize) {
        println(it)
        it.allUnique()
    }.indexOf(true) + markerSize

    println(result)
}

private fun CharSequence.allUnique() : Boolean =
    toSet().count() == length

