package additional

import java.io.File

val wordsFile = File("words.txt")

fun main() {

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
        val learnTimes = 3
        println("Меню:\n1 – Учить слова\n2 – Статистика\n0 – Выход")
        when (readln()) {
            "1" -> {
                val randomWords =
                    dictionary.filter { it.correctAnswersCount < learnTimes }.shuffled().take(answerVariants)
                        .toMutableList()
                if (randomWords.isEmpty()) {
                    println("Вы выучили все слова")
                    break
                } else if (randomWords.size < answerVariants)
                    randomWords += dictionary.filter { it.correctAnswersCount >= learnTimes }.shuffled()
                        .take(answerVariants - randomWords.size)

                val originalWord = randomWords.filter { it.correctAnswersCount < learnTimes }.random().original
                println(originalWord)
                randomWords.forEachIndexed { index, randomWord ->
                    print(
                        "${index + 1} - ${randomWord.translate}" +
                                if (index != randomWords.lastIndex) ", " else "\n"
                    )
                }

                val word = randomWords[randomWords.indexOfFirst { it.original == originalWord }]
                when (readln()) {
                    "0" -> break
                    (randomWords.indexOf(word) + 1).toString() -> {
                        println("Правильно!")
                        dictionary[dictionary.indexOfFirst { it.original == originalWord }].correctAnswersCount++
                        saveDictionary(dictionary)
                    }

                    else -> println("Неправильно - слово ${word.translate}")
                }
            }

            "2" -> statistics(dictionary, learnTimes)

            "0" -> break
            else -> println("Неверно введенный номер пукта")
        }
    }
}

fun statistics(dictionary: MutableList<Word>, learnTimes: Int) {
    val learned = dictionary.filter { it.correctAnswersCount >= learnTimes }.size
    val total = dictionary.size
    val percent = learned * 100 / total
    println("Выучено $learned из $total слов | $percent%")
}

fun saveDictionary(dictionary: MutableList<Word>) {
    val wordsFile = File("words.txt")
    wordsFile.writeText("")
    for (word in dictionary)
        wordsFile.appendText("${word.original}|${word.translate}|${word.correctAnswersCount}\n")
}

