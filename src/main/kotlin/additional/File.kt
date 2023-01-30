package additional

import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()

    val listOfWords = wordsFile.readLines()
    val dictionary = addWords(listOfWords)
    dictionary.forEach { println(it) }
}

fun addWords(listOfWords: List<String>): MutableList<Word>{
    val dictionary = mutableListOf<Word>()

    for (i in listOfWords.indices) {
        val wordParameters = listOfWords[i].split("|")
        if (wordParameters.size > 1) {
            dictionary.add(
                Word(
                    original = wordParameters[0] ?: " ", // тут не уверен, проверяется ли на null
                    translate = wordParameters[1] ?: " ", // тут не уверен, проверяется ли на null
                    correctAnswersCount = try {
                        wordParameters[2].toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                )
            )
        }
    }
    return dictionary
}