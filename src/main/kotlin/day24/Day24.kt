package day24

import day24.Cell.Companion.DOWN_BLIZZARD
import day24.Cell.Companion.EMPTY
import day24.Cell.Companion.LEFT_BLIZZARD
import day24.Cell.Companion.RIGHT_BLIZZARD
import day24.Cell.Companion.UP_BLIZZARD
import day24.Cell.Companion.WALL
import java.io.File

fun main() {
    val lines = File("files/day24/input.txt").inputStream().bufferedReader().readLines()

    val stateZero = State.of(lines)

    val states = States.initStates(stateZero)

    val entryRow = 0
    val entryCol = 1

    val exitRow = states.getState(0).matrix.size - 1
    val exitCol = states.getState(0).matrix[0].size - 2

    val tripOne = explore(states, 0, entryRow, entryCol, exitRow, exitCol)

    println(tripOne)

    val tripTwo = explore(states, tripOne, exitRow, exitCol, entryRow, entryCol)

    println("${tripTwo - tripOne}")

    val tripThree = explore(states, tripTwo, entryRow, entryCol, exitRow, exitCol)

    println("${tripThree - tripTwo}")

    println("Total: $tripThree")
}

fun explore(states: States, minute: Int, startRow: Int, startCol: Int, finalRow: Int, finalCol: Int): Int {
    val queue = ArrayDeque( listOf(Expedition(startRow, startCol, minute)))
    val explored = mutableSetOf(queue[0]) // including both space and time

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()

        // current is the final destination, we can return
        if (current.isPos(finalRow, finalCol))
            return current.minute

        val nextState = states.getState(current.minute + 1)

        if (current.canGoUp(nextState)) {
            val candidate = current.up()
            if (candidate !in explored) {
                explored += candidate
                queue += candidate
            }
        }

        if (current.canGoDown(nextState)) {
            val candidate = current.down()
            if (candidate !in explored) {
                explored += candidate
                queue += candidate
            }
        }

        if (current.canGoLeft(nextState)) {
            val candidate = current.left()
            if (candidate !in explored) {
                explored += candidate
                queue += candidate
            }
        }

        if (current.canGoRight(nextState)) {
            val candidate = current.right()
            if (candidate !in explored) {
                explored += candidate
                queue += candidate
            }
        }

        if (current.canStay(nextState)) {
            val candidate = current.stay()
            if (candidate !in explored) {
                explored += candidate
                queue += candidate
            }
        }
    }

    throw Exception("No path was found")
}

data class Expedition(val row: Int, val col: Int, val minute: Int) {
    fun isPos(finalRow: Int, finalCol: Int) = row == finalRow && col == finalCol

    fun canGoUp(state: State): Boolean {
        if (row == 0) return false
        if (state[row - 1][col] == EMPTY) return true
        return false
    }

    fun canGoDown(state: State): Boolean {
        if (row == state.numRows - 1) return false
        if (state[row + 1][col] == EMPTY) return true
        return false
    }

    fun canGoLeft(state: State): Boolean {
        if (col == 1) return false
        if (state[row][col - 1] == EMPTY) return true
        return false
    }

    fun canGoRight(state: State): Boolean {
        if (col == state.numCols - 2) return false
        if (state[row][col + 1] == EMPTY) return true
        return false
    }

    fun canStay(state: State) = state[row][col] == EMPTY

    fun up()    = Expedition(row - 1, col, minute + 1)
    fun down()  = Expedition(row + 1, col, minute + 1)
    fun left()  = Expedition(row, col - 1, minute + 1)
    fun right() = Expedition(row, col + 1, minute + 1)
    fun stay()  = Expedition(row, col, minute + 1)
}

class States(val numStates: Int) {
    private var states: MutableMap<Int, State> = hashMapOf()

    fun getState(s: Int): State {
        if (states[s % numStates] != null)
            return states[s % numStates]!!

        val prevState = states[s-1]!!

        val state = prevState.calcNextState()

        states[s] = state
/*
        println("STATE $s")
        state.print()
        println("")
*/
        return state
    }

    companion object {
        fun initStates(stateZero: State): States {
            val numStates = leastCommonMultiple(stateZero.numRows, stateZero.numCols)

            val states = States(numStates)
            /*
            println("STATE 0")
            stateZero.print()
            println("")
            */

            states.states[0] = stateZero
            return states
        }
    }
}

class State(val matrix: Array<Array<Cell>>) {
    val numRows = matrix.size
    val numCols = matrix[0].size

    fun calcNextState(): State {
        val newMatrix = arrayOfNulls<Array<Cell>>(numRows)
        matrix.forEachIndexed { rowIndex, row ->
            if (rowIndex == 0 || rowIndex == numRows - 1)
                newMatrix[rowIndex] = row
            else {
                val newRow = arrayOfNulls<Cell>(numCols)
                row?.forEachIndexed { colIndex, cell ->
                    if (colIndex == 0 || colIndex == numCols - 1)
                        newRow[colIndex] = cell
                    else {
                        val cellUp = if (rowIndex == 1) matrix[numRows - 2]!![colIndex] else matrix[rowIndex - 1]!![colIndex]
                        val cellDown = if (rowIndex == numRows - 2) matrix[1]!![colIndex] else matrix[rowIndex + 1]!![colIndex]
                        val cellLeft = if (colIndex == 1) matrix[rowIndex]!![numCols - 2] else matrix[rowIndex]!![colIndex - 1]
                        val cellRight = if (colIndex == numCols - 2) matrix[rowIndex]!![1] else matrix[rowIndex]!![colIndex + 1]
                        newRow[colIndex] = Cell(cellDown.hasUp, cellUp.hasDown, cellRight.hasLeft, cellLeft.hasRight, false)
                    }
                }
                newMatrix[rowIndex] = newRow as Array<Cell>
            }
        }
        return State(newMatrix as Array<Array<Cell>>)
    }

    operator fun get(row: Int): Array<Cell> = matrix[row]

    fun print() {
        matrix.forEach { array -> array.forEach { it.print() } ; println() }
    }

    companion object {
        fun of(lines: List<String>): State {
            val matrix = arrayOfNulls<Array<Cell>>(lines.size)

            matrix[0] = Array(lines[0].length) { WALL }
            matrix[0]!![1] = EMPTY

            matrix[lines.size - 1] = Array(lines[0].length) { WALL }
            matrix[lines.size - 1]!![lines[0].length - 2] = EMPTY

            lines.drop(1).dropLast(1).forEachIndexed { index, line ->
                val array = arrayOfNulls<Cell>(line.length)
                line.removeSurrounding("#").forEachIndexed { index, char ->
                    array[index + 1] = when (char) {
                        '>' -> RIGHT_BLIZZARD
                        '<' -> LEFT_BLIZZARD
                        '^' -> UP_BLIZZARD
                        'v' -> DOWN_BLIZZARD
                        else -> EMPTY
                    }
                }
                array[0] = WALL
                array[line.length - 1] = WALL

                matrix[index + 1] = array as Array<Cell>
            }

            return State(matrix as Array<Array<Cell>>)
        }
    }
}

data class Cell (
    val hasUp: Boolean,
    val hasDown: Boolean,
    val hasLeft: Boolean,
    val hasRight: Boolean,
    val isWall: Boolean,
        ) {
    fun print() {
        if (isWall) { print("#") ; return }

        var count = 0
        if (hasUp) count++
        if (hasDown) count++
        if (hasLeft) count++
        if (hasRight) count++

        if (count == 0) print(".")
        else if (count > 1) print(count)
        else {
            if (hasUp) print("^")
            else if (hasDown) print("v")
            else if (hasLeft) print("<")
            else if (hasRight) print(">")
        }
    }

    companion object {
        val WALL = Cell(false, false, false, false, true)
        val UP_BLIZZARD = Cell(true, false, false, false, false)
        val DOWN_BLIZZARD = Cell(false, true, false, false, false)
        val LEFT_BLIZZARD = Cell(false, false, true, false, false)
        val RIGHT_BLIZZARD = Cell(false, false, false, true, false)
        val EMPTY = Cell(false, false, false, false, false)
    }
}

private fun leastCommonMultiple(a: Int, b: Int): Int {
    return a * b / greatestCommonDivisor(a, b)
}

private fun greatestCommonDivisor(a: Int, b: Int): Int {
    var a = a
    var b = b
    var temp: Int
    while (b != 0) {
        temp = b
        b = a.mod(b)
        a = temp
    }
    return a
}