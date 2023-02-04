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
        println("Меню:\n1 – Учить слова\n2 – Статистика\n0 – Выход")
        when (readln()) {
            "1" -> {
                while (true) {
                    println("Хотите выучить новое слово?")
                    if (readln() == "Да".lowercase()) {
                        val unlearnedWords = dictionary.filter { it.correctAnswersCount < 3 }
                        if (unlearnedWords.isEmpty()) {
                            println("Вы выучили все слова")
                            break
                        } else {
                            val randomWords = unlearnedWords.shuffled().take(4)
                            val word = randomWords.random().original
                            println("Выберите правильный перевод слова $word")
                            randomWords.forEach { println("${randomWords.indexOf(it) + 1}. ${it.translate}") }
                        }
                    } else break
                }
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

