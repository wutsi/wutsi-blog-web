package com.wutsi.blog.app.config

import com.wutsi.blog.sdk.PinApi
import com.wutsi.blog.sdk.Sdk
import com.wutsi.blog.sdk.WutsiEnvironment
import com.wutsi.core.http.Http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WutsiSdkConfiguration(
        private val http: Http,
        private val env: Environment
) : WebMvcConfigurer {
    @Bean
    fun pinApi(): PinApi =
        sdk().pinApi()

    @Bean
    fun sdk(): Sdk =
            Sdk(
                    http = http,
                    environment = environment()
            )

    private fun environment() : WutsiEnvironment {
        if (env.acceptsProfiles("prod"))
            return WutsiEnvironment.prod
        else if (env.acceptsProfiles("int"))
            return WutsiEnvironment.int
        else
            return WutsiEnvironment.local
    }
}
