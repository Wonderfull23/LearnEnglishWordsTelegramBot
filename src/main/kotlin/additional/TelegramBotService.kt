package additional


import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val STATISTICS = "statistics_clicked"
const val LEARN_WORDS = "learn_words_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

class TelegramBotService {
    private val client: HttpClient = HttpClient.newBuilder().build()
    fun getUpdates(botToken: String, updateId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(botToken: String, chatId: String?, text: String?): String? {
        val encoded = URLEncoder.encode(
            text,
            StandardCharsets.UTF_8
        )


        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(botToken: String, chatId: String?): String? {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": "$LEARN_WORDS"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$STATISTICS"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendQuestion(botToken: String, chatId: String?, question: Question?): String? {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
        val answers = question?.variants?.mapIndexed { index, word ->
            """
        {
            "text": "${word.translate}",
            "callback_data": "$CALLBACK_DATA_ANSWER_PREFIX$index"
        }
        """.trimIndent()
        }?.joinToString(separator = ",")
        val sendAnswersBody = """
            {
                "chat_id": $chatId,
                "text": "${question?.correctAnswer?.original}",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            ${answers?.trimStart()}
                        ]
                    ]
                }
            }
        """.trimIndent()

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendAnswersBody))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}

