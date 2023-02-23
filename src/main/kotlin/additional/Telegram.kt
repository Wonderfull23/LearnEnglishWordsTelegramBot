package additional

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val bot = TelegramBotService()

    val updateIdIdRegex = "\"update_id\":(\\d+?),".toRegex()
    val chatIdRegex = "\"chat\":\\{\"id\":(\\d+?),".toRegex()
    val textRegex = "\"text\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates = bot.getUpdates(botToken, updateId)
        println(updates)

        val updateIdString = updateIdIdRegex.find(updates)?.groups?.get(1)?.value
        if (updateIdString != null) println(updateIdString)
        updateId = (updateIdString?.toInt() ?: (updateId - 1)) + 1
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value
        val text = textRegex.find(updates)?.groups?.get(1)?.value
        if (text == "Hello") bot.sendMessage(botToken, chatId, text)
    }
}




