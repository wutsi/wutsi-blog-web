package com.wutsi.blog.app.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.stats.StatsApi
import com.wutsi.stats.StatsApiBuilder
import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@Configuration
class StatsApiConfiguration(
    @Autowired private val env: Environment,
    @Autowired private val mapper: ObjectMapper,
    @Autowired private val tracingRequestInterceptor: RequestInterceptor
) {
    @Bean
    fun statsApi(): StatsApi =
        StatsApiBuilder()
            .build(
                env = statsEnvironment(),
                mapper = mapper,
                interceptors = listOf(tracingRequestInterceptor)
            )

    fun statsEnvironment(): com.wutsi.stats.Environment =
        if (env.acceptsProfiles(Profiles.of("prod")))
            com.wutsi.stats.Environment.PRODUCTION
        else
            com.wutsi.stats.Environment.SANDBOX
}
