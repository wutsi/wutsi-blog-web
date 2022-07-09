package com.wutsi.blog.app.config

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.wutsi.platform.security.apikey.ApiKeyRequestInterceptor
import com.wutsi.stats.StatsApi
import com.wutsi.stats.StatsApiBuilder
import com.wutsi.stream.ObjectMapperBuilder
import com.wutsi.tracing.TracingRequestInterceptor
import feign.Request
import feign.Retryer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import java.util.concurrent.TimeUnit.MILLISECONDS

@Configuration
class StatsApiConfiguration(
    @Autowired private val env: Environment,
    @Autowired private val tracingRequestInterceptor: TracingRequestInterceptor,
    @Autowired private val apiKeyRequestInterceptor: ApiKeyRequestInterceptor
) {
    @Bean
    fun statsApi(): StatsApi {
        val mapper = ObjectMapperBuilder().build()
        mapper.registerModule(ParameterNamesModule())
        mapper.registerModule(Jdk8Module())
        mapper.registerModule(JavaTimeModule())

        return StatsApiBuilder()
            .build(
                env = statsEnvironment(),
                mapper = mapper,
                interceptors = listOf(tracingRequestInterceptor, apiKeyRequestInterceptor),
                retryer = Retryer.NEVER_RETRY,
                options = Request.Options(
                    500L,
                    MILLISECONDS,
                    500L,
                    MILLISECONDS,
                    false
                )
            )
    }

    fun statsEnvironment(): com.wutsi.stats.Environment =
        if (env.acceptsProfiles(Profiles.of("prod")))
            com.wutsi.stats.Environment.PRODUCTION
        else
            com.wutsi.stats.Environment.SANDBOX
}
