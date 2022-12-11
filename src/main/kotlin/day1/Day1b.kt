package day1

import java.io.File
import java.io.InputStream
import kotlin.math.max

fun main() {
    val inputStream: InputStream = File("files/day1.txt").inputStream()
    var currentCounter = 0
    val list = mutableListOf<Int>()

    inputStream.bufferedReader()
        .forEachLine {
        if (it.isEmpty()) {
            list.add(currentCounter)
            currentCounter = 0
        }
        else {
            currentCounter += it.toInt()
        }
    }
    println(list
        .sortedDescending()
        .take(3)
        .sum())
}
