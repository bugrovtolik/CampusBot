import com.elbekD.bot.Bot
import com.elbekD.bot.feature.chain.chain
import com.elbekD.bot.feature.chain.jumpTo
import com.elbekD.bot.types.Chat
import com.elbekD.bot.types.ReplyKeyboardRemove
import com.google.api.services.sheets.v4.model.ValueRange
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class CommandController(private val bot: Bot, private val sheetsUtil: GoogleSheetsUtil, private val markupUtil: MarkupUtil) {
    fun start() {
        bot.chain("/start", predicate = { msg -> !sheetsUtil.getStudents().any { it.id == msg.chat.id.toString() } && msg.chat.id != System.getenv("feedbackChatId").toLong() }) { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.GREETING).join()
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_FIRST_NAME)
            sheetsUtil.updateColumn("A", msg.chat.id, msg.chat.id.toString())
        }.then { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_LAST_NAME)
            sheetsUtil.updateColumn("B", msg.chat.id, msg.text.toString())
        }.then { msg ->
            val student = sheetsUtil.getStudents().first { it.id == msg.chat.id.toString() }
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_UNIVERSITY.format(student.firstName), markup = markupUtil.getUniversitiesMarkup())
            sheetsUtil.updateColumn("C", msg.chat.id, msg.text.toString())
        }.then { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_YEAR_STUDY, markup = markupUtil.getYearStudyMarkup())
            sheetsUtil.updateColumn("I", msg.chat.id, msg.text.toString())
        }.then("checkYear") { msg ->
            if (msg.text?.toIntOrNull() in 1..6) {
                bot.sendMessage(msg.chat.id, MessageTexts.ASK_STUD_PRO, markup = markupUtil.getStudProMarkup())
                sheetsUtil.updateColumn("J", msg.chat.id, msg.text.toString())
            } else {
                bot.sendMessage(msg.chat.id, MessageTexts.DEFAULT)
                bot.jumpTo("checkYear", msg)
            }
        }.then { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_WHO_I_AM).join()
            bot.sendMessage(msg.chat.id, MessageTexts.MINI_WHO_I_AM, markup = markupUtil.getYesNoExtendedMarkup())
            sheetsUtil.updateColumn("K", msg.chat.id, msg.text.toString())
        }.then { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_LECTORIUM, markup = markupUtil.getYesNoExtendedMarkup())
            if (msg.text == MessageTexts.YES_EXTENDED) sheetsUtil.updateColumn("G", msg.chat.id, "'+")
        }.then { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.ASK_RULES).join()
            bot.sendMessage(msg.chat.id, MessageTexts.RULES, markup = markupUtil.getAgreeRulesMarkup())
            if (msg.text == MessageTexts.YES_EXTENDED) sheetsUtil.updateColumn("H", msg.chat.id, "'+")
        }.then("checkAgreed") { msg ->
            if (msg.text == MessageTexts.AGREE) {
                bot.sendMessage(msg.chat.id, MessageTexts.FINISH_REGISTRATION, markup = ReplyKeyboardRemove(true))
                sheetsUtil.updateColumn("D", msg.chat.id, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
                sheetsUtil.updateColumn("F", msg.chat.id, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
                sheetsUtil.updateColumn("E", msg.chat.id, "1")
            } else {
                bot.sendMessage(msg.chat.id, MessageTexts.FORCE_AGREE)
                bot.jumpTo("checkAgreed", msg)
            }
        }.build()
    }

    fun checkin() {
        bot.chain("/checkin") { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.WANNA_CHECKIN, markup = markupUtil.getYesNoMarkup())
        }.then { msg ->
            if (msg.text == MessageTexts.YES && !sheetsUtil.checkedInToday(msg.chat.id)) {
                bot.sendMessage(msg.chat.id, MessageTexts.THANKS, markup = ReplyKeyboardRemove(true))
                val student = sheetsUtil.getStudents().first { it.id == msg.chat.id.toString() }
                sheetsUtil.updateColumn("D", msg.chat.id, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
                sheetsUtil.updateColumn("E", msg.chat.id, student.checkinCount?.toInt()?.plus(1).toString())
            } else {
                bot.sendMessage(msg.chat.id, MessageTexts.ALREADY_CHECKED_IN, markup = ReplyKeyboardRemove(true))
            }
        }.build()
    }

    fun subscribe() {
        bot.onCommand("/subscribe") { msg, _ ->
            bot.sendMessage(msg.chat.id, MessageTexts.SUBSCRIBED, markup = markupUtil.getSubscribeMarkup())
        }
    }

    fun feedback() {
        bot.chain("/feedback") { msg ->
            bot.sendMessage(msg.chat.id, MessageTexts.WANNA_FEEDBACK, markup = markupUtil.getNoMarkup())
        }.then { msg ->
            if (msg.text == MessageTexts.NO) {
                bot.sendMessage(msg.chat.id, MessageTexts.OK, markup = ReplyKeyboardRemove(true))
            } else {
                bot.sendMessage(msg.chat.id, MessageTexts.RESULT_FEEDBACK, markup = ReplyKeyboardRemove(true))
                bot.forwardMessage(System.getenv("feedbackChatId").toLong(), msg.chat.id, msg.message_id)
            }
        }.build()

        bot.onMessage { msg ->
            if (msg.chat.id == System.getenv("feedbackChatId").toLong()) {
                msg.reply_to_message?.forward_from?.id?.let { bot.sendMessage(it, msg.text.toString()) }
            } else {
                bot.sendMessage(msg.chat.id, bot.getMyCommands().join()
                    .joinToString("\n", "${MessageTexts.DEFAULT}\n") { "/${it.command} - ${it.description}" })
            }
        }
    }

    fun whoiam() {
        bot.onCommand("/whoiam") { msg, _ ->
            bot.sendMessage(msg.chat.id, MessageTexts.WHO_I_AM, markup = markupUtil.getWhoIAmMarkup())
        }
    }
}
