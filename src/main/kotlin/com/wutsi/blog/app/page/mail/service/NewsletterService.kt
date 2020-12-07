package com.wutsi.blog.app.page.mail.service

import com.wutsi.blog.sdk.NewsletterApi
import org.springframework.stereotype.Component


@Component
class NewsletterService (
        private val api: NewsletterApi
){
    fun unsubscribe(email: String, userId: Long?= null) {
        if (userId == null)
            api.unsubscribe(email)
        else
            api.unsubscribe(userId, email)
    }
}
