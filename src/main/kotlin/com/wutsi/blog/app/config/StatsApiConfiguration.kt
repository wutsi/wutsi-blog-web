package com.wutsi.blog.app.config

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.wutsi.stats.StatsApi
import com.wutsi.stats.StatsApiBuilder
import com.wutsi.stream.ObjectMapperBuilder
import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@Configuration
class StatsApiConfiguration(
    @Autowired private val env: Environment,
    @Autowired private val tracingRequestInterceptor: RequestInterceptor
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
                interceptors = listOf(tracingRequestInterceptor)
            )
    }

    fun statsEnvironment(): com.wutsi.stats.Environment =
        if (env.acceptsProfiles(Profiles.of("prod")))
            com.wutsi.stats.Environment.PRODUCTION
        else
            com.wutsi.stats.Environment.SANDBOX
}
