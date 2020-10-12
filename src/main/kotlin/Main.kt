import MessageTexts.Companion.AGREE
import MessageTexts.Companion.ALREADY_CHECKED_IN
import MessageTexts.Companion.ASK_FIRST_NAME
import MessageTexts.Companion.ASK_LAST_NAME
import MessageTexts.Companion.ASK_RULES
import MessageTexts.Companion.ASK_STUD_PRO
import MessageTexts.Companion.ASK_UNIVERSITY
import MessageTexts.Companion.ASK_YEAR_STUDY
import MessageTexts.Companion.CHECKIN
import MessageTexts.Companion.DEFAULT
import MessageTexts.Companion.FEEDBACK
import MessageTexts.Companion.FINISH_REGISTRATION
import MessageTexts.Companion.FORCE_AGREE
import MessageTexts.Companion.GREETING
import MessageTexts.Companion.NO
import MessageTexts.Companion.OK
import MessageTexts.Companion.RESULT_FEEDBACK
import MessageTexts.Companion.RULES
import MessageTexts.Companion.SUBSCRIBE
import MessageTexts.Companion.SUBSCRIBED
import MessageTexts.Companion.THANKS
import MessageTexts.Companion.WANNA_CHECKIN
import MessageTexts.Companion.WANNA_FEEDBACK
import MessageTexts.Companion.WHOIAM
import MessageTexts.Companion.WHO_I_AM_INFO
import MessageTexts.Companion.YES
import com.elbekD.bot.Bot
import com.elbekD.bot.feature.chain.chain
import com.elbekD.bot.feature.chain.jumpTo
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun main() {
    val bot = Bot.createPolling("@KyivCampusBot", System.getenv("botToken"))
    val sheetsUtil = GoogleSheetsUtil()
    val markupUtil = MarkupUtil()

    bot.chain("/start", predicate = { msg -> !sheetsUtil.getStudents().any { it.id == msg.chat.id.toString() } && msg.chat.id != System.getenv("feedbackChatId").toLong() }) { msg ->
        bot.sendMessage(msg.chat.id, GREETING).join()
        bot.sendMessage(msg.chat.id, ASK_FIRST_NAME)
        sheetsUtil.updateColumn("A", msg.chat.id, msg.chat.id.toString())
    }.then { msg ->
        bot.sendMessage(msg.chat.id, ASK_LAST_NAME)
        sheetsUtil.updateColumn("B", msg.chat.id, msg.text.toString())
    }.then { msg ->
        val student = sheetsUtil.getStudents().first { it.id == msg.chat.id.toString() }
        bot.sendMessage(msg.chat.id, ASK_UNIVERSITY.format(student.firstName), markup = markupUtil.getUniversitiesMarkup())
        sheetsUtil.updateColumn("C", msg.chat.id, msg.text.toString())
    }.then { msg ->
        bot.sendMessage(msg.chat.id, ASK_YEAR_STUDY, markup = markupUtil.getYearStudyMarkup())
        sheetsUtil.updateColumn("G", msg.chat.id, msg.text.toString())
    }.then("checkYear") { msg ->
        if (msg.text?.toIntOrNull() in 1..6) {
            bot.sendMessage(msg.chat.id, ASK_STUD_PRO, markup = markupUtil.getStudProMarkup())
            sheetsUtil.updateColumn("H", msg.chat.id, msg.text.toString())
        } else {
            bot.sendMessage(msg.chat.id, DEFAULT)
            bot.jumpTo("checkYear", msg)
        }
    }.then { msg ->
        bot.sendMessage(msg.chat.id, ASK_RULES).join()
        bot.sendMessage(msg.chat.id, RULES, markup = markupUtil.getAgreeRulesMarkup())
        sheetsUtil.updateColumn("I", msg.chat.id, msg.text.toString())
    }.then("checkAgreed") { msg ->
        if (msg.text == AGREE) {
            bot.sendMessage(msg.chat.id, FINISH_REGISTRATION, markup = markupUtil.getDefaultMarkup())
            sheetsUtil.updateColumn("D", msg.chat.id, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
            sheetsUtil.updateColumn("F", msg.chat.id, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
            sheetsUtil.updateColumn("E", msg.chat.id, "1")
        } else {
            bot.sendMessage(msg.chat.id, FORCE_AGREE)
            bot.jumpTo("checkAgreed", msg)
        }
    }.build()

    bot.chain(label = CHECKIN, predicate = { msg -> msg.text == CHECKIN }) { msg ->
        bot.sendMessage(msg.chat.id, WANNA_CHECKIN, markup = markupUtil.getYesNoMarkup())
    }.then { msg ->
        if (msg.text == YES) {
            if (!sheetsUtil.checkedInToday(msg.chat.id)) {
                bot.sendMessage(msg.chat.id, THANKS, markup = markupUtil.getDefaultMarkup())
                val student = sheetsUtil.getStudents().first { it.id == msg.chat.id.toString() }
                sheetsUtil.updateColumn("D", msg.chat.id, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
                sheetsUtil.updateColumn("E", msg.chat.id, student.checkinCount?.toInt()?.plus(1).toString())
            } else {
                bot.sendMessage(msg.chat.id, ALREADY_CHECKED_IN, markup = markupUtil.getDefaultMarkup())
            }
        } else {
            bot.sendMessage(msg.chat.id, OK, markup = markupUtil.getDefaultMarkup())
        }
    }.build()

    bot.chain(label = FEEDBACK, predicate = { msg -> msg.text == FEEDBACK }) { msg ->
        bot.sendMessage(msg.chat.id, WANNA_FEEDBACK, markup = markupUtil.getNoMarkup())
    }.then { msg ->
        if (msg.text == NO) {
            bot.sendMessage(msg.chat.id, OK, markup = markupUtil.getDefaultMarkup())
        } else {
            bot.sendMessage(msg.chat.id, RESULT_FEEDBACK, markup = markupUtil.getDefaultMarkup())
            bot.forwardMessage(System.getenv("feedbackChatId").toLong(), msg.chat.id, msg.message_id)
        }
    }.build()

    bot.onMessage { msg ->
        when (msg.text) {
            WHOIAM -> bot.sendMessage(msg.chat.id, WHO_I_AM_INFO, markup = markupUtil.getWhoIAmMarkup())
            SUBSCRIBE -> bot.sendMessage(msg.chat.id, SUBSCRIBED, markup = markupUtil.getSubscribeMarkup())
            else -> {
                if (msg.chat.id == System.getenv("feedbackChatId").toLong()) {
                    msg.reply_to_message?.forward_from?.id?.let { bot.sendMessage(it, msg.text.toString()) }
                } else {
                    bot.sendMessage(msg.chat.id, DEFAULT, markup = markupUtil.getDefaultMarkup())
                }
            }
        }
    }

    bot.start()
}
