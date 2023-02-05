package additional

import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()

    val listOfWords = wordsFile.readLines()
    val dictionary = createDirectory(listOfWords)
    menu(dictionary)
}

fun createDirectory(listOfWords: List<String>): MutableList<Word> {
    val dictionary = mutableListOf<Word>()

    for (i in listOfWords) {
        val wordParameters = i.split("|")
        if (wordParameters.size > 1)
            dictionary.add(
                Word(
                    original = wordParameters[0],
                    translate = wordParameters[1],
                    correctAnswersCount = wordParameters[2].toIntOrNull() ?: 0
                )
            )
    }
    return dictionary
}

fun menu(dictionary: MutableList<Word>) {
    while (true) {
        val answerVariants = 4
        println("Меню:\n1 – Учить слова\n2 – Статистика\n0 – Выход")
        when (readln()) {
            "1" -> {
                val randomWords =
                    dictionary.filter { it.correctAnswersCount < 3 }.shuffled().take(answerVariants).toMutableList()
                if (randomWords.isEmpty()) {
                    println("Вы выучили все слова")
                    break
                } else if (randomWords.size < answerVariants)
                    randomWords += dictionary.filter { it.correctAnswersCount > 3 }.shuffled()
                        .take(answerVariants - randomWords.size)

                val word = randomWords.filter { it.correctAnswersCount < 3 }.random().original
                println(word)
                randomWords.forEachIndexed { index, randomWord -> print("$index - ${randomWord.translate}" +
                        if (index != randomWords.lastIndex) ", " else "\n") }
            }

            "2" -> {
                val learned = dictionary.filter { it.correctAnswersCount > 3 }.size
                val total = dictionary.size
                val percent = learned * 100 / total
                println("Выучено $learned из $total слов | $percent%")
            }

            "0" -> break
            else -> println("Неверно введенный номер пукта")
        }
    }
}

