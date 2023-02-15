package additional

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

fun Question.printQuestion() {
    println(this.correctAnswer.original)
    this.variants.forEachIndexed { index, randomWord ->
        print("${index + 1} - ${randomWord.translate}, ")
    }
    println("0 - Выход")
}

fun main() {
    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозмоно загрузить словарь")
        return
    }

    showMenu(trainer)
}

fun showMenu(trainer: LearnWordsTrainer) {
    while (true) {
        println("Меню: 1 – Учить слова, 2 – Статистика, 0 – Выход")
        when (readln()) {
            "1" -> {
                while (true) {
                    val question = trainer.getNextQuestion()

                    if (question == null) {
                        println("Вы выучили все слова")
                        break
                    }
                    question.printQuestion()
                    val userInput = readln()
                    if (userInput == "0") break
                    if (trainer.checkAnswer(question, userInput)) println("Правильно!")
                    else println("Неправильно - слово ${question.correctAnswer.translate}")
                }
            }

            "2" -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%")
            }

            "0" -> break
            else -> println("Введите 1, 2 или 0")
        }
    }
}




