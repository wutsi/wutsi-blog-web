package com.wutsi.blog.app.page.mail.service

import com.wutsi.blog.app.common.service.SiteProvider
import com.wutsi.email.event.EmailEventType
import com.wutsi.email.event.UnsubscriptionSubmittedEventPayload
import com.wutsi.stream.EventStream
import org.springframework.stereotype.Component
import java.net.URLDecoder

@Component
class NewsletterService(
    private val eventStream: EventStream,
    private val siteProvider: SiteProvider
) {
    fun unsubscribe(email: String, userId: Long? = null) {
        eventStream.publish(
            type = EmailEventType.UNSUBSCRIPTION_SUBMITTED.urn,
            payload = UnsubscriptionSubmittedEventPayload(
                userId = userId,
                email = URLDecoder.decode(email, "utf-8"),
                siteId = siteProvider.siteId()
            )
        )
    }
}
