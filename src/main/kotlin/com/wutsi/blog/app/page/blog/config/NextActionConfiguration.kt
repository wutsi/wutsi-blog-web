package com.wutsi.blog.app.page.blog.config

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.service.NextActionSet
import com.wutsi.blog.app.page.blog.service.nba.BiographyNextAction
import com.wutsi.blog.app.page.blog.service.nba.LinkedInNextAction
import com.wutsi.blog.app.page.blog.service.nba.NewsletterNextAction
import com.wutsi.blog.app.page.blog.service.nba.TwitterNextAction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NextActionConfiguration(private val requestContext: RequestContext) {

    @Bean
    fun nextActionSet(): NextActionSet {
        return NextActionSet(actions = listOf(
            biographyNextAction(),
            newsletterNextAction(),
            twitterNextAction(),
            linkedInNextAction()
        ))
    }

    @Bean
    fun biographyNextAction() = BiographyNextAction(requestContext)

    @Bean
    fun linkedInNextAction() = LinkedInNextAction(requestContext)

    @Bean
    fun twitterNextAction() = TwitterNextAction(requestContext)

    @Bean
    fun newsletterNextAction() = NewsletterNextAction(requestContext)
}
