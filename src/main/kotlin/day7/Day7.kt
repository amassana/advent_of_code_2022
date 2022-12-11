package day7

import java.io.BufferedReader
import java.io.File
import java.io.InputStream

fun main() {
    val inputStream: InputStream = File("files/day7/input.txt").inputStream()

    val root = Directory("/")

    val reader = inputStream.bufferedReader()
    reader.readLine() // skip the first "$ cd /"
    loadData(root, reader)

    // solution 1
    println(find(root, 100000))

    // solution 2 -- root will never be a candidate, though ...
    val needed = 30000000
    val available = 70000000 - root.size()
    val missing = needed - available

    val candidates = root.childDirectoriesLargerOrEqualThan(missing).sortedBy { it.size() }
    val winner = candidates.first()
    val winnerSize = winner.size()
    val winnerSize2 = root.childDirectoriesLargerOrEqualThan(missing).minOf { it.size() }
    println("Missing $missing ${winner.name} $winnerSize $winnerSize2")
}

private fun find(currentDirectory: Directory, maxSize: Int) : Int {
    var sum = 0
    if (currentDirectory.size() <= maxSize)
        sum += currentDirectory.size()
    for (directory in currentDirectory.directories()) {
        sum += find(directory, maxSize)
    }
    return sum
}

private fun loadData(currentDirectory: Directory, reader: BufferedReader) {
    while (true) {
        val line = reader.readLine()
        if (line == null) // EOF
            return
        else if (line == "\$ ls")
            continue
        else if (line == "\$ cd ..")
            return
        else if (line.startsWith("dir ")) {
            val childDirectory = Directory(line.substring(4))
            currentDirectory.addChild(childDirectory)
        } else if (line.startsWith("\$ cd ")) {
            loadData(currentDirectory.getDirectory(line.substring(5)), reader)
        } else {
            val parts = line.split(" ")
            val childFile = File(parts[1], parts[0].toInt())
            currentDirectory.addChild(childFile)
        }
    }
}

sealed class Node(var name: String) {
    abstract fun size(): Int
}

class File (name: String, private val size: Int) : Node(name) {
    override fun size(): Int = size
}

class Directory(name: String) : Node(name) {
    private val children = mutableListOf<Node>()

    override fun size() : Int = children.sumOf { it.size() }

    fun addChild(n: Node) = children.add(n)

    // could be a hashmap
    fun getDirectory(name: String): Directory =
        children.find { it.name == name } as Directory

    fun directories(): Iterator<Directory> =  children.filterIsInstance<Directory>().iterator()

    fun childDirectoriesLargerOrEqualThan(threshold: Int) : List<Directory> {
        val descendantDirectories = children
            .filterIsInstance<Directory>()
            .flatMap { it.childDirectoriesLargerOrEqualThan(threshold) }
        val childrenDirectories = children
            .filterIsInstance<Directory>()
            .filter { it.size() >= threshold }
        return descendantDirectories + childrenDirectories
    }
}
