package com.wutsi.blog.app.page.blog.service.nba

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.model.UserModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NewsletterNextActionTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @InjectMocks
    private lateinit var nba: NewsletterNextAction

    @Test
    fun getWeekday() {
        assertNull(nba.get(UserModel(newsletterDeliveryDayOfWeek = 1)))
        assertNull(nba.get(UserModel(newsletterDeliveryDayOfWeek = 2)))
        assertNull(nba.get(UserModel(newsletterDeliveryDayOfWeek = 3)))
        assertNull(nba.get(UserModel(newsletterDeliveryDayOfWeek = 4)))
        assertNull(nba.get(UserModel(newsletterDeliveryDayOfWeek = 5)))
        assertNull(nba.get(UserModel(newsletterDeliveryDayOfWeek = 6)))
        assertNull(nba.get(UserModel(newsletterDeliveryDayOfWeek = 7)))
    }

    @Test
    fun getNoWeekday() {
        testNoNewsletter(0)
        testNoNewsletter(-1)
    }

    private fun testNoNewsletter(weekday: Int) {
        doReturn("This is title").whenever(requestContext).getMessage(any(), eq(null), eq(null), eq(null))

        val result = nba.get(UserModel(newsletterDeliveryDayOfWeek = weekday))

        assertEquals("newsletter", result?.name)
        assertEquals("/assets/wutsi/img/newsletter.png", result?.iconUrl)
        assertEquals("/me/settings?highlight=newsletter-container#newsletter", result?.url)
        assertEquals("This is title", result?.title)
    }
}
