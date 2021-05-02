package com.wutsi.blog.app.config

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.wutsi.earning.EarningApi
import com.wutsi.earning.EarningApiBuilder
import com.wutsi.security.apikey.ApiKeyRequestInterceptor
import com.wutsi.stream.ObjectMapperBuilder
import com.wutsi.tracing.TracingRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@Configuration
class EarningApiConfiguration(
    @Autowired private val env: Environment,
    @Autowired private val tracingRequestInterceptor: TracingRequestInterceptor,
    @Autowired private val apiKeyRequestInterceptor: ApiKeyRequestInterceptor
) {
    @Bean
    fun earningApi(): EarningApi {
        val mapper = ObjectMapperBuilder().build()
        mapper.registerModule(ParameterNamesModule())
        mapper.registerModule(Jdk8Module())
        mapper.registerModule(JavaTimeModule())

        return EarningApiBuilder()
            .build(
                env = earningEnvironment(),
                mapper = mapper,
                interceptors = listOf(tracingRequestInterceptor, apiKeyRequestInterceptor)
            )
    }

    fun earningEnvironment(): com.wutsi.earning.Environment =
        if (env.acceptsProfiles(Profiles.of("prod")))
            com.wutsi.earning.Environment.PRODUCTION
        else
            com.wutsi.earning.Environment.SANDBOX
}
