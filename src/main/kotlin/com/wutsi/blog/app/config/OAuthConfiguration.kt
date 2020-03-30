package com.wutsi.blog.app.config

import com.github.scribejava.apis.FacebookApi
import com.github.scribejava.apis.GitHubApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth20Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OAuthConfiguration {
    companion object {
        const val GITHUB_OAUTH_SERVICE: String = "GITHUB_OAUTH_SERVICE"
        const val FACEBOOK_OAUTH_SERVICE: String = "FACEBOOK_OAUTH_SERVICE"
    }



    @Bean(GITHUB_OAUTH_SERVICE)
    fun getGithubOAuthService(
            @Value ("\${wutsi.oauth.github.client-id}") clientId: String,
            @Value ("\${wutsi.oauth.github.client-secret}") clientSecret: String,
            @Value ("\${wutsi.oauth.github.callback-url}") callbackUrl: String
    ): OAuth20Service = ServiceBuilder(clientId)
            .apiSecret(clientSecret)
            .callback(callbackUrl)
            .build(GitHubApi.instance())


    @Bean(FACEBOOK_OAUTH_SERVICE)
    fun getFacebookOAuthService(
            @Value ("\${wutsi.oauth.facebook.client-id}") clientId: String,
            @Value ("\${wutsi.oauth.facebook.client-secret}") clientSecret: String,
            @Value ("\${wutsi.oauth.facebook.callback-url}") callbackUrl: String
    ): OAuth20Service = ServiceBuilder(clientId)
            .apiSecret(clientSecret)
            .callback(callbackUrl)
            .build(FacebookApi.instance())

}
