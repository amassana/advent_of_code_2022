package day8

import java.io.File
import java.io.InputStream
import kotlin.math.max

fun main() {
    val inputStream: InputStream = File("files/day8/input.txt").inputStream()

    val lines = inputStream.bufferedReader().readLines()

    one(lines)
    //two(lines)
}

fun check(lines: List<String>, row: Int, column: Int): Int {
    // check upwards
    var counterA = checkColumn(lines, row, column, row - 1 downTo  0)
    // check to the right
    var counterB = checkRow(lines, row, column, column + 1 until lines.size)
    // check downwards
    var counterC = checkColumn(lines, row, column, row + 1 until lines.size)
    // check leftwards
    var counterD = checkRow(lines, row, column, column - 1 downTo 0)

    return counterA * counterB * counterC * counterD
}

fun checkRow(lines: List<String>, row: Int, column: Int, intProgression: IntProgression): Int {
    var counter = 0
    for (i in intProgression) {
        counter ++
        if (lines[row][i].code >= lines[row][column].code)
            break
    }
    return counter
}
fun checkColumn(lines: List<String>, row: Int, column: Int, intProgression: IntProgression): Int {
    var counter = 0
    for (i in intProgression) {
        counter ++
        if (lines[i][column].code >= lines[row][column].code)
            break
    }
    return counter
}

fun two(lines: List<String>) {
    var max : Int = -1
    for (row in lines.indices) {
        for (column in lines.indices) {
            val value = check(lines, row, column)
            max = max(value, max)
        }
    }
    println(max)
}

fun one(lines: List<String>) {
    var isVisible = Array(lines.size) { Array(lines.size) { false }  }

    var counter = 0

    for (row in lines.indices) {
        var currentRowMax = -1
        for (column in lines.indices) {
            if (lines[row][column].code > currentRowMax) {
                currentRowMax = lines[row][column].code
                isVisible[row][column] = true
            }
        }
    }
    for (row in lines.indices) {
        var currentRowMax = -1
        for (column in lines.indices.reversed()) {
            if (lines[row][column].code > currentRowMax) {
                currentRowMax = lines[row][column].code
                isVisible[row][column] = true
            }
        }
    }
    for (column in lines.indices) {
        var currentRowMax = -1
        for (row in lines.indices) {
            if (lines[row][column].code > currentRowMax) {
                currentRowMax = lines[row][column].code
                isVisible[row][column] = true
            }
        }
    }
    for (column in lines.indices) {
        var currentRowMax = -1
        for (row in lines.indices.reversed()) {
            if (lines[row][column].code > currentRowMax) {
                currentRowMax = lines[row][column].code
                isVisible[row][column] = true
            }
        }
    }

    for (column in isVisible) {
        for (row in column) {
            if (row) counter++
        }
    }
/*
    isVisible.forEach {
        it.forEach { if (it) print('T') else print('-') }
        println()
    }*/

    println(counter)
}
