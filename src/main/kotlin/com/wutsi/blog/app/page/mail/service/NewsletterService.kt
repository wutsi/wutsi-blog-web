package com.wutsi.blog.app.page.mail.service

import com.wutsi.blog.app.common.service.SiteProvider
import com.wutsi.email.event.EmailEventType
import com.wutsi.email.event.UnsubscriptionSubmittedEventPayload
import com.wutsi.stream.EventStream
import org.springframework.stereotype.Component

@Component
class NewsletterService(
    private val emailEventStream: EventStream,
    private val siteProvider: SiteProvider
) {
    fun unsubscribe(email: String, userId: Long? = null) {
        emailEventStream.publish(
            type = EmailEventType.UNSUBSCRIPTION_SUBMITTED.urn,
            payload = UnsubscriptionSubmittedEventPayload(
                userId = userId,
                email = email,
                siteId = siteProvider.siteId()
            )
        )
    }
}
