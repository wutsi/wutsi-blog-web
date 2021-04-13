package com.wutsi.blog.app.page.mail

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.email.event.EmailEventType
import com.wutsi.email.event.UnsubscriptionSubmittedEventPayload
import com.wutsi.stream.EventStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.verify
import org.springframework.boot.test.mock.mockito.MockBean

class MailControllerTest : SeleniumTestSupport() {
    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun unsubscribeFromBlog() {
        driver.get("$url/mail/unsubscribe?u=3&email=foo@gmail.com")

        assertElementPresent(".navbar-blog")
        assertElementNotPresent(".navbar-site")
        assertCurrentPageIs(PageName.MAIL_UNSUBSCRIBE)

        val payload = argumentCaptor<UnsubscriptionSubmittedEventPayload>()
        verify(eventStream).enqueue(
            eq(EmailEventType.UNSUBSCRIPTION_SUBMITTED.urn),
            payload.capture()
        )
        assertEquals("foo@gmail.com", payload.firstValue.email)
        assertEquals(1L, payload.firstValue.siteId)
        assertEquals(3L, payload.firstValue.userId)
    }

    @Test
    fun unsubscribeFromWutsi() {
        driver.get("$url/mail/unsubscribe?email=foo@gmail.com")

        assertElementNotPresent(".navbar-blog")
        assertElementPresent(".navbar-site")
        assertCurrentPageIs(PageName.MAIL_UNSUBSCRIBE)

        val payload = argumentCaptor<UnsubscriptionSubmittedEventPayload>()
        verify(eventStream).enqueue(
            eq(EmailEventType.UNSUBSCRIPTION_SUBMITTED.urn),
            payload.capture()
        )
        assertEquals("foo@gmail.com", payload.firstValue.email)
        assertEquals(1L, payload.firstValue.siteId)
        assertNull(payload.firstValue.userId)
    }
}
