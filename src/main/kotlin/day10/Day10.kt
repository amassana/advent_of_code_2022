package day10

import java.io.File
import java.io.InputStream

fun main () {
    val inputStream: InputStream = File("files/day10/input.txt").inputStream()

    val cpu = CPU()

    inputStream.bufferedReader().forEachLine { line ->
        val operation = Operation.parse(line)

        cpu.execute(operation)
    }

    //println(cpu.total)

    cpu.screen.chunked(40).forEach { println(it) }
}

class CPU {
    var register = 1
    var cycle = 1
    var total = 0
    var screen = StringBuilder(300)

    fun execute(operation: Operation) {
        if (operation is Noop) {
            draw()
            cycle++
            //updateTotal()
        }
        else if (operation is Add) {
            draw()
            cycle++
            //updateTotal()
            draw()
            register += operation.n
            cycle++
            //updateTotal()
        }
    }

    private fun updateTotal() {
        if (cycle == 20 || cycle == 60 || cycle == 100 || cycle == 140 || cycle == 180 || cycle == 220) {
            total += register * cycle
        }
    }

    private fun draw() {
        //if (register == (cycle - 1) % 40 || register == (cycle - 1) % 40 - 1 || register == (cycle - 1) % 40 + 1)
        if (register in (cycle - 1) % 40 - 1 .. (cycle - 1) % 40 + 1)
            screen += "#"
        else
            screen += " "
    }
}

private operator fun StringBuilder.plusAssign(s: String) {
    this.append(s)
}

sealed class Operation {
    companion object {
        fun parse(s: String): Operation {
            if (s.startsWith("n"))
                return Noop
            return Add.of(s)
        }
    }
}

object Noop : Operation()

data class Add(val n: Int) : Operation() {
    companion object {
        fun of(s: String): Add {
            val parts = s.split(" ")
            return Add(parts[1].toInt())
        }
    }
}
