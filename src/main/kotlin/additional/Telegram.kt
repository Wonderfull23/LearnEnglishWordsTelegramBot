package additional

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val bot = TelegramBotService()

    while (true) {
        Thread.sleep(2000)
        val updates = bot.getUpdates(botToken, updateId)
        println(updates)
        val updateIdIdRegex = "\"update_id\":(\\d+?),".toRegex()
        val matchResult = updateIdIdRegex.find(updates)
        val updateIdString = matchResult?.groups?.get(1)?.value
        if (updateIdString != null) println(updateIdString)
        updateId = (updateIdString?.toInt() ?: (updateId - 1)) + 1

        val chatIdRegex = "\"chat\":\\{\"id\":(\\d+?),".toRegex()
        val matchChatIdResult = chatIdRegex.find(updates)
        val chatId = matchChatIdResult?.groups?.get(1)?.value

        val textRegex = "\"text\":\"(.+?)\"".toRegex()
        val matchTextResult = textRegex.find(updates)
        val text = matchTextResult?.groups?.get(1)?.value

        if (text == "Hello") bot.sendMessage(botToken, chatId, text)
    }
}




