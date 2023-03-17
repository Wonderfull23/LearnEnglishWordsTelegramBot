import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val bot = TelegramBotService()

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String? = null,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

fun main(args: Array<String>) {
    val botToken = args[0]
    var lastUpdateId = 0L
    val trainers = HashMap<Long, LearnWordsTrainer>()
    val json = Json { ignoreUnknownKeys = true }

    while (true) {
        Thread.sleep(2000)
        val responseString = bot.getUpdates(botToken, lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, json, botToken, trainers) }
        lastUpdateId = sortedUpdates.last().updateId + 1
    }
}

fun handleUpdate(
    update: Update,
    json: Json,
    botToken: String,
    trainers: HashMap<Long, LearnWordsTrainer>
) {

    val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
    val message = update.message?.text
    val data = update.callbackQuery?.data

    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }

    if (message == "/start") bot.sendMenu(json, botToken, chatId)
    if (data == STATISTICS) {
        bot.sendMessage(json, botToken, chatId, trainer.getStatistics().toString())
        bot.sendMenu(json, botToken, chatId)
    } else if (data == RESET_STATISTICS) {
        trainer.resetProgress()
        bot.sendMessage(json, botToken, chatId, "Прогресс сброшен")
    }
        else if (data == LEARN_WORDS) {
        checkNextQuestionAndSend(json, trainer, botToken, chatId)
    } else if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
        val index = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()

        if (trainer.lastQuestion?.let { trainer.checkAnswer(it, index + 1) } == true)
            bot.sendMessage(json, botToken, chatId, "Правильно")
        else
            bot.sendMessage(
                json,
                botToken,
                chatId,
                "Не правильно: ${trainer.lastQuestion?.correctAnswer?.original} - ${trainer.lastQuestion?.correctAnswer?.translate}"
            )
        trainer.lastQuestion = checkNextQuestionAndSend(json, trainer, botToken, chatId)
    }
}


fun checkNextQuestionAndSend(json: Json, trainer: LearnWordsTrainer, botToken: String, chatId: Long): Question? {

    val question = trainer.getNextQuestion()
    if (question == null) {
        bot.sendMessage(json, botToken, chatId, "Вы выучили все слова в базе")
        bot.sendMenu(json, botToken, chatId)
    } else bot.sendQuestion(json, botToken, chatId, question)
    return question
}




