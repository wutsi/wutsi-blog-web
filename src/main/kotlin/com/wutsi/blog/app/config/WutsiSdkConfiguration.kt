package com.wutsi.blog.app.config

import com.wutsi.blog.sdk.ChannelApi
import com.wutsi.blog.sdk.FollowerApi
import com.wutsi.blog.sdk.NewsletterApi
import com.wutsi.blog.sdk.PinApi
import com.wutsi.blog.sdk.Sdk
import com.wutsi.blog.sdk.ShareApi
import com.wutsi.blog.sdk.TagApi
import com.wutsi.blog.sdk.TopicApi
import com.wutsi.blog.sdk.WutsiEnvironment
import com.wutsi.core.http.Http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class WutsiSdkConfiguration(
    private val http: Http,
    private val env: Environment
) {
    @Bean
    fun channelApi(): ChannelApi =
        sdk().channelApi()

    @Bean
    fun followerApi(): FollowerApi =
        sdk().followerApi()

    @Bean
    fun newsletter(): NewsletterApi =
        sdk().newsletterApi()

    @Bean
    fun pinApi(): PinApi =
        sdk().pinApi()

    @Bean
    fun shareApi(): ShareApi =
        sdk().shareApi()
    @Bean
    fun tagApi(): TagApi =
        sdk().tagApi()

    @Bean
    fun topicApi(): TopicApi =
        sdk().topicApi()
    @Bean
    fun sdk(): Sdk =
        Sdk(
            http = http,
            environment = environment()
        )

    private fun environment(): WutsiEnvironment {
        if (env.acceptsProfiles("prod"))
            return WutsiEnvironment.prod
        else if (env.acceptsProfiles("int"))
            return WutsiEnvironment.int
        else
            return WutsiEnvironment.local
    }
}
