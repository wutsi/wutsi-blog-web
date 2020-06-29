package com.wutsi.blog.app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.FixedLocaleResolver
import java.util.Locale

@Configuration
class I18nConfiguration {
    @Bean
    fun localeResolver() : LocaleResolver = FixedLocaleResolver(Locale("fr", "CM"))
}
