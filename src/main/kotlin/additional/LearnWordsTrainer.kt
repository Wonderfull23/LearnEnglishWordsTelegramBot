package additional

import java.io.File

class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
)

data class Question(
    var variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(private val learnedAnswerCount: Int = 3, private val countOfQuestionWords: Int = 4) {
    private val wordsFile = File("words.txt")
    private val listOfWords = wordsFile.readLines()
    private val dictionary = createDirectory()

    fun getStatistics(): Statistics {
        val learned = dictionary.filter { it.correctAnswersCount >= learnedAnswerCount }.size
        val total = dictionary.size
        val percent = learned * 100 / total
        return Statistics(learned, total, percent)
    }

    fun getNextQuestion(): Question? {
        val randomWordsToLearn = dictionary.filter { it.correctAnswersCount < learnedAnswerCount }
        val variants: MutableList<Word> = randomWordsToLearn.shuffled().take(countOfQuestionWords).toMutableList()
        if (variants.isEmpty()) return null
        else if (variants.size < countOfQuestionWords) {
            variants += dictionary.filter { it.correctAnswersCount >= learnedAnswerCount }
                .shuffled()
                .take(countOfQuestionWords - variants.size)
        }
        val correctAnswer =
            variants.filter { it.correctAnswersCount < learnedAnswerCount }.random()
        return Question(variants, correctAnswer)
    }

    fun checkAnswer(question: Question, userInput: String): Boolean {
        return if (userInput == question.variants.indexOf(question.correctAnswer).plus(1).toString()) {
            question.correctAnswer.correctAnswersCount++
            saveDictionary(dictionary)
            true
        } else false
    }

    private fun createDirectory(): List<Word> {
        val dictionary = mutableListOf<Word>()
        for (i in listOfWords) {
            val wordParameters = i.split("|")
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

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        for (word in dictionary)
            wordsFile.appendText("${word.original}|${word.translate}|${word.correctAnswersCount}\n")
    }
}


