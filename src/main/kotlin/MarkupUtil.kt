import com.elbekD.bot.types.*

class MarkupUtil {
    private val universities = listOf("КНУ ім.Т.Г.Шевченко", "НУХТ", "НУБіП", "КНУКіМ", "НУФВСУ", "КНЛУ")
    val yesNoCommands = mapOf(Pair("yes", "Так"), Pair("no", "Ні"))
    val defaultCommands = mapOf(Pair("/checkin", "Зачекінитися"),
                                Pair("/subscribe", "Підписатися на новини"),
                                Pair("/feedback", "Залишити відгук"))

    fun getDefaultMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(defaultCommands.values.map { listOf(KeyboardButton(it)) })
    }

    fun getYesNoMarkup(): ReplyKeyboard {
        return InlineKeyboardMarkup(listOf(yesNoCommands.values.map { InlineKeyboardButton(it) }))
    }

    fun getUniversitiesMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(universities.map { listOf(KeyboardButton(it)) })
    }

    fun getYearStudyMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(listOf((1..6).toList().map { KeyboardButton(it.toString()) }))
    }

    fun getStudProMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(listOf(
            "Отримав флаєр",
            "Реклама в інтернеті",
            "Через соцмережі Instagram/Facebook",
            "Через канал в Telegram",
            "Розповіли друзі"
        ).map { listOf(KeyboardButton(it)) })
    }
}
