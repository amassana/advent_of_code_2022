package day20

import java.io.File

fun main() {

    val inList = mutableListOf<Node>()
    val decryptedList = mutableListOf<Node>()

    File("files/day20/input.txt").inputStream().bufferedReader().lineSequence()
        .forEach {
            var node = Node(it.toLong() * 811589153L)
            inList += node
            decryptedList += node
        }

    //decryptedList.forEachIndexed { i, n -> println("$i\t${n.value}")}
    //println("-----")

    repeat(10) {
        inList.forEach { node ->
            val index = decryptedList.indexOf(node)
            if (node.value != 0L) {
                decryptedList.removeAt(index)
                var newIndex = (index + node.value)
                if (newIndex > inList.size)
                    newIndex %= (inList.size - 1)
                if (newIndex < 0) {
                    newIndex %= (inList.size - 1)
                    if (newIndex < 0)
                        newIndex += (inList.size - 1)
                }
                decryptedList.add(newIndex.toInt(), node)
            }

            //decryptedList.forEachIndexed { i, n -> println("$i\t${n.value}")}
            //println("-----")
        }
    }

    val zeroIndex = decryptedList.indexOfFirst { it.value == 0L }

    val a = (zeroIndex + 1000) % inList.size
    val b = (zeroIndex + 2000) % inList.size
    val c = (zeroIndex + 3000) % inList.size

    val vA = decryptedList.elementAt(a).value
    val vB = decryptedList.elementAt(b).value
    val vC = decryptedList.elementAt(c).value

    println("$zeroIndex")
    println("$a $vA")
    println("$b $vB")
    println("$c $vC")
    println("${vA + vB + vC}")
}

data class Node (val value: Long) {
    override fun equals(other: Any?) = other === this
}
