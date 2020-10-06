import com.elbekD.bot.Bot

fun main() {
    val bot = Bot.createPolling("@KyivCampusBot", System.getenv("botToken"))
    val controller = CommandController(bot, GoogleSheetsUtil(), MarkupUtil())

    controller.start()
    controller.checkin()
    controller.subscribe()
    controller.feedback()

    bot.start()
}
