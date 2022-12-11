package day1

import java.io.File
import java.io.InputStream
import kotlin.math.max

fun main() {
    val inputStream: InputStream = File("files/day1.txt").inputStream()
    var currentCounter: Int = 0
    var maxCounter: Int = 0

    inputStream.bufferedReader().forEachLine {
        if (it.isEmpty()) {
            maxCounter = max(currentCounter, maxCounter)
            currentCounter = 0
        }
        else {
            currentCounter += it.toInt()
        }
    }
    println(maxCounter)
}
