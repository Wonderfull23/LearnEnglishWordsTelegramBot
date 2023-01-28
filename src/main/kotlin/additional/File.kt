package additional

import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()
    val listOfWords = wordsFile.readLines()
    listOfWords.forEach { println(it) }
}