import com.elbekD.bot.types.*

class MarkupUtil {
    private val universities = listOf("КНУ ім.Т.Г.Шевченко", "НУХТ", "НУБіП", "КНУКіМ", "НУФВСУ", "КНЛУ")
    private val studPro = listOf("Отримав флаєр", "Реклама в інтернеті", "Через соцмережі Instagram/Facebook", "Через канал в Telegram", "Розповіли друзі")
    val whoIAm = mapOf(Pair("Реєстрація", "[Реєстрація](https://docs.google.com/forms/d/e/1FAIpQLSfWfOBE2w3eFYX250FG4ByQAEMYf540dWMZ1UVycLqbY2lwgg/viewform)"), Pair("Більше інформації", "[Більше інформації](http://kyivcampus.tilda.ws/whoiam)"))
    val yesNoCommands = mapOf(Pair("yes", "Так"), Pair("no", "Ні"))

    fun getYesNoMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(listOf(yesNoCommands.values.map { KeyboardButton(it) }))
    }

    fun getNoMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(listOf(listOf(KeyboardButton(yesNoCommands["no"].toString()))))
    }

    fun getUniversitiesMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(universities.map { listOf(KeyboardButton(it)) })
    }

    fun getYearStudyMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(listOf((1..6).toList().map { KeyboardButton(it.toString()) }))
    }

    fun getStudProMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(studPro.map { listOf(KeyboardButton(it)) })
    }

    fun getWhoIAmMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(whoIAm.keys.map { listOf(KeyboardButton(it)) })
    }

}
