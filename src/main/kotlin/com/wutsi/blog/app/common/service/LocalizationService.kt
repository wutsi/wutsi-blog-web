package com.wutsi.blog.app.common.service

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service


@Service
class LocalizationService(
        private val messages: MessageSource
) {
    fun getLocale() = LocaleContextHolder.getLocale()

    fun getMessage(key: String, args: Array<Any>? = null): String {
        val locale = getLocale()
        return messages.getMessage(key, args, locale)
    }
}
