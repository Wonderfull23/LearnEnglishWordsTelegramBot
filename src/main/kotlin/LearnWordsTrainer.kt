import kotlinx.serialization.Serializable
import java.io.File
import java.lang.IllegalStateException
import java.lang.IndexOutOfBoundsException

@Serializable
data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
) {
    override fun toString(): String = "Выучено $learned из $total слов | $percent%"

}

data class Question(
    var variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val fileName: String = "words.txt",
    private val learnedAnswerCount: Int = 3,
    private val countOfQuestionWords: Int = 4) {

    private val wordsFile = File(fileName)
    var lastQuestion: Question? = null
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
        lastQuestion = Question(variants, correctAnswer)
        return Question(variants, correctAnswer)
    }

    fun checkAnswer(question: Question?, userInput: Int): Boolean {
        return if (userInput == question?.variants?.indexOf(question.correctAnswer)?.plus(1)) {
            question.correctAnswer.correctAnswersCount++
            saveDictionary()
            true
        } else false
    }

    private fun createDirectory(): List<Word> {
        try {
            val wordsFile = File(fileName)
            if (!wordsFile.exists()) {
                File("words.txt").copyTo(wordsFile)
            }
            val dictionary = mutableListOf<Word>()
            wordsFile.readLines().forEach(){
                val splitLine = it.split("|")
                dictionary.add(Word(splitLine[0], splitLine[1], splitLine[2].toIntOrNull() ?: 0))
            }
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException ("некорректный файл")
        }
    }

    private fun saveDictionary() {
        val wordsFile = File(fileName)
        wordsFile.writeText("")
        for (word in dictionary)
            wordsFile.appendText("${word.original}|${word.translate}|${word.correctAnswersCount}\n")
    }
    fun resetProgress() {
        dictionary.forEach{it.correctAnswersCount = 0}
        saveDictionary()
    }

}


