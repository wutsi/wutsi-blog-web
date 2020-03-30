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

    @Value ("\${wutsi.oauth.github.client-id}")
    private lateinit var githubClientId: String

    @Value ("\${wutsi.oauth.github.client-secret}")
    private lateinit var githubClientSecret: String

    @Value ("\${wutsi.oauth.facebook.client-id}")
    private lateinit var facebookClientId: String

    @Value ("\${wutsi.oauth.facebook.client-secret}")
    private lateinit var facebookClientSecret: String

    @Bean(GITHUB_OAUTH_SERVICE)
    fun getGithubOAuthService(): OAuth20Service = ServiceBuilder(githubClientId)
            .apiSecret(githubClientSecret)
            .build(GitHubApi.instance())


    @Bean(FACEBOOK_OAUTH_SERVICE)
    fun getFacebookOAuthService(): OAuth20Service = ServiceBuilder(facebookClientId)
            .apiSecret(facebookClientId)
            .build(FacebookApi.instance())

}
