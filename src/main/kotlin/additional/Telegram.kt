package additional

val bot = TelegramBotService()

fun main(args: Array<String>) {
    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозмоно загрузить словарь")
        return
    }

    val botToken = args[0]
    var updateId = "0"

    val updateIdIdRegex = "\"update_id\":(\\d+?),".toRegex()
    val chatIdRegex = "\"chat\":\\{\"id\":(\\d+?),".toRegex()
    val textRegex = "\"text\":\"(.+?)\"".toRegex()
    val dataRegex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates = bot.getUpdates(botToken, updateId)
        println(updates)
        val updateIdString = updateIdIdRegex.find(updates)?.groups?.get(1)?.value
        if (updateIdString != null) println(updateIdString)
        updateId = (((updateIdString?.toInt() ?: (updateId.toInt() - 1)) + 1).toString())
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value
        val text = textRegex.find(updates)?.groups?.get(1)?.value
        val data = dataRegex.find(updates)?.groups?.get(1)?.value
        if (text?.lowercase() == "/start") bot.sendMenu(botToken, chatId)
        if (data?.lowercase() == statistics) {
            bot.sendMessage(botToken, chatId, trainer.getStatistics().toString())
            bot.sendMenu(botToken, chatId)
        } else if (data == learnWords) {
            trainer.lastQuestion = checkNextQuestionAndSend(trainer, botToken, chatId)
        }
        else if (data != null && startsWith(data, CALLBACK_DATA_ANSWER_PREFIX)) {
            val index = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()

            if (trainer.lastQuestion != null && trainer.checkAnswer(trainer.lastQuestion!!, index + 1))
                bot.sendMessage(botToken, chatId, "Правильно")
            else
                bot.sendMessage(
                    botToken,
                    chatId,
                    "Не правильно: ${trainer.lastQuestion?.correctAnswer?.original} - ${trainer.lastQuestion?.correctAnswer?.translate}"
                )
                trainer.lastQuestion = checkNextQuestionAndSend(trainer, botToken, chatId)
        }

    }
}


fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, botToken: String, chatId: String?): Question? {

    val question = trainer.getNextQuestion()
    if (question == null) {
        bot.sendMessage(botToken, chatId, "Вы выучили все слова в базе")
        bot.sendMenu(botToken, chatId)
    } else bot.sendQuestion(botToken, chatId, question)
    return question
}

fun startsWith(str: String?, prefix: String): Boolean {
    return str?.startsWith(prefix) == true
}


