package com.wutsi.blog.app.config

import com.wutsi.blog.app.common.service.LocaleResolverImpl
import com.wutsi.blog.app.common.service.RequestContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver

@Configuration
class I18nConfiguration {
    @Bean
    fun localeResolver(requestContext: RequestContext): LocaleResolver =
        LocaleResolverImpl(requestContext)
}
