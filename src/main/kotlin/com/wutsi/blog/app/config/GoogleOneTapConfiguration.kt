package com.wutsi.blog.app.config

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GoogleOneTapConfiguration {
    @Bean
    fun httpTransport() : HttpTransport {
        return NetHttpTransport()
    }

    @Bean
    fun jsonFactory() : JsonFactory {
        return JacksonFactory()
    }
}
