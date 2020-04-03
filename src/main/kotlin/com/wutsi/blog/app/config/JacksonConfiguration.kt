package com.wutsi.blog.app.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration


@Configuration
class JacksonConfiguration {
    fun objectMapper () : ObjectMapper {
        return ObjectMapper()
    }
}
