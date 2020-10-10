import com.elbekD.bot.types.*

class MarkupUtil {
    private val universities = listOf("КНУ ім.Т.Г.Шевченко", "НУХТ", "НУБіП", "КНУКіМ", "НУФВСУ", "КНЛУ")
    private val studPro = listOf("Отримав флаєр", "Реклама в інтернеті", "Через соцмережі Instagram/Facebook", "Через канал в Telegram", "Розповіли друзі")

    fun getYesNoMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(listOf(listOf(KeyboardButton(MessageTexts.YES)), listOf(KeyboardButton(MessageTexts.NO))))
    }

    fun getYesNoExtendedMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(listOf(listOf(KeyboardButton(MessageTexts.YES_EXTENDED)), listOf(KeyboardButton(MessageTexts.NO_EXTENDED))))
    }

    fun getNoMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(listOf(listOf(KeyboardButton(MessageTexts.NO))))
    }

    fun getAgreeRulesMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(listOf(listOf(KeyboardButton(MessageTexts.AGREE))))
    }

    fun getUniversitiesMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(universities.map { listOf(KeyboardButton(it)) })
    }

    fun getYearStudyMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup((1..6).toList().map { listOf(KeyboardButton(it.toString())) })
    }

    fun getStudProMarkup(): ReplyKeyboard {
        return ReplyKeyboardMarkup(studPro.map { listOf(KeyboardButton(it)) })
    }

    fun getWhoIAmMarkup(): ReplyKeyboard {
        return getInviteLinkMarkup(listOf(
            Pair(MessageTexts.WHO_I_AM_REGISTRATION_TEXT, MessageTexts.WHO_I_AM_REGISTRATION_LINK),
            Pair(MessageTexts.WHO_I_AM_MORE_INFO_TEXT, MessageTexts.WHO_I_AM_MORE_INFO_LINK)
        ))
    }

    fun getSubscribeMarkup(): ReplyKeyboard {
        return getInviteLinkMarkup(listOf(Pair(MessageTexts.INVITE_CAMPUS_CHANNEL_TEXT, MessageTexts.INVITE_CAMPUS_CHANNEL_LINK)))
    }

    private fun getInviteLinkMarkup(textLinks: List<Pair<String, String>>): ReplyKeyboard {
        return InlineKeyboardMarkup(listOf(textLinks.map { InlineKeyboardButton(it.first, it.second) }))
    }
}
