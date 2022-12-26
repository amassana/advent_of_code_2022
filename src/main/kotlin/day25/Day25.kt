package day25

import java.io.File

fun main() {
    var total = 0L
    File("files/day25/input.txt").inputStream().bufferedReader().forEachLine {
        val snafu = FiveBase.toLong(it)
        val back = FiveBase.toFiveBase(snafu)
        println("$it\t\t$snafu\t\t${it == back}")
        total += snafu
    }
    println(total)

    println(FiveBase.toFiveBase(total))
}