import com.elbekD.bot.Bot
import com.elbekD.bot.feature.chain.chain
import com.elbekD.bot.feature.chain.jumpTo
import com.elbekD.bot.feature.chain.jumpToAndFire
import com.elbekD.bot.types.ReplyKeyboardMarkup
import com.google.api.services.sheets.v4.model.ValueRange
import java.time.LocalDate

class CommandController(private val bot: Bot, private val sheetsUtil: GoogleSheetsUtil, private val markupUtil: MarkupUtil) {
    private val feedbackChatId: Long = -340647063

    fun start() {
        bot.chain("/start", predicate = { msg -> !sheetsUtil.getStudents().any { it.id == msg.chat.id.toString() } && msg.chat.id != feedbackChatId }) { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.GREETING).get()
            bot.sendMessage(msg.chat.id, MessageTexts.FREE_SPACE).get()
            bot.sendMessage(msg.chat.id, MessageTexts.COMMUNITY).get()
            bot.sendMessage(msg.chat.id, MessageTexts.REGISTER).get()
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_FIRST_NAME)

            sheetsUtil.appendToSheet("Students", ValueRange().setValues(listOf(listOf(msg.chat.id.toString()))))
        }.then { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_LAST_NAME)
            sheetsUtil.updateColumn("B", msg.chat.id, msg.text ?: "")
        }.then { msg ->
            val students = sheetsUtil.getStudents()
            val student = students.first { it.id == msg.chat.id.toString() }
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_UNIVERSITY.format(student.firstName), markup = markupUtil.getUniversitiesMarkup())
            sheetsUtil.updateColumn("C", msg.chat.id, msg.text ?: "")
        }.then { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_YEAR_STUDY, markup = markupUtil.getYearStudyMarkup())
            sheetsUtil.updateColumn("D", msg.chat.id, msg.text ?: "")
        }.then("checkYear") { msg ->
            when (msg.text?.toIntOrNull() ?: 0) {
                in 1..6 -> bot.jumpToAndFire("askStudPro", msg)
                else -> {
                    bot.sendMessage(msg.chat.id, MessageTexts.DEFAULT, markup = markupUtil.getYearStudyMarkup())
                    bot.jumpTo("checkYear", msg)
                }
            }
        }.then("askStudPro") { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_STUD_PRO, markup = markupUtil.getStudProMarkup())
            sheetsUtil.updateColumn("E", msg.chat.id, msg.text ?: "")
        }.then { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.FINISH_REGISTRATION, markup = markupUtil.getDefaultMarkup())
            sheetsUtil.updateColumn("F", msg.chat.id, msg.text ?: "")
            sheetsUtil.updateColumn("G", msg.chat.id, LocalDate.now().toString())
        }.build()
    }

    fun checkin() {
        bot.chain(markupUtil.defaultCommands["/checkin"].toString()) { msg ->
            if (sheetsUtil.checkedInToday(msg.chat.id)) {
                bot.sendMessage(msg.chat.id, MessageTexts.ALREADY_CHECKED_IN, markup = markupUtil.getDefaultMarkup())
            } else {
                bot.sendMessage(msg.chat.id, MessageTexts.THANKS, markup = markupUtil.getDefaultMarkup())
                sheetsUtil.appendToSheet( "Check-ins", ValueRange().setValues(listOf(listOf(msg.chat.id.toString(), LocalDate.now().toString()))))
            }
        }.build()
    }

    fun subscribe() {
        bot.chain(markupUtil.defaultCommands["/subscribe"].toString()) { msg ->
            if (sheetsUtil.isSubscribed(msg.chat.id)) {
                bot.sendMessage(msg.chat.id, MessageTexts.WANNA_UNSUBSCRIBE, markup = markupUtil.getYesNoMarkup())
                bot.jumpTo("unsubscribe", msg)
            } else {
                bot.sendMessage(msg.chat.id, MessageTexts.WANNA_SUBSCRIBE, markup = markupUtil.getYesNoMarkup())
                bot.jumpTo("subscribe", msg)
            }
        }.then("subscribe", isTerminal = true) { msg ->
            when (msg.text) {
                markupUtil.yesNoCommands["yes"] -> {
                    bot.sendMessage(msg.chat.id, MessageTexts.SUBSCRIBED, markup = markupUtil.getDefaultMarkup())
                    sheetsUtil.updateColumn("H", msg.chat.id, "'+")
                }
                markupUtil.yesNoCommands["no"] -> bot.sendMessage(msg.chat.id, MessageTexts.OK, markup = markupUtil.getDefaultMarkup())
                else -> bot.sendMessage(msg.chat.id, MessageTexts.DEFAULT, markup = markupUtil.getDefaultMarkup())
            }
        }.then("unsubscribe", isTerminal = true) { msg ->
            when (msg.text) {
                markupUtil.yesNoCommands["yes"] -> {
                    bot.sendMessage(msg.chat.id, MessageTexts.UNSUBSCRIBED, markup = markupUtil.getDefaultMarkup())
                    sheetsUtil.updateColumn("H", msg.chat.id, "")
                }
                markupUtil.yesNoCommands["no"] -> bot.sendMessage(msg.chat.id, MessageTexts.OK, markup = markupUtil.getDefaultMarkup())
                else -> bot.sendMessage(msg.chat.id, MessageTexts.DEFAULT, markup = markupUtil.getYesNoMarkup())
            }
        }.build()
    }

    fun feedback() {
        bot.chain(markupUtil.defaultCommands["/feedback"].toString()) { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.WANNA_FEEDBACK, markup = ReplyKeyboardMarkup(listOf(listOf())))
        }.then { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.RESULT_FEEDBACK, markup = markupUtil.getDefaultMarkup())
            bot.forwardMessage(feedbackChatId, msg.chat.id, msg.message_id)
        }.build()
    }
}
