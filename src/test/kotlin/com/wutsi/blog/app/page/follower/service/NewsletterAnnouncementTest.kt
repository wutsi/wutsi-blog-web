package com.wutsi.blog.app.page.follower.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.Toggles
import com.wutsi.blog.app.page.partner.service.PartnerService
import com.wutsi.blog.app.page.partner.service.WPPAnnouncement
import com.wutsi.blog.app.page.payment.service.ContractService
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.PageName
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NewsletterAnnouncementTest {
    @Mock private lateinit var requestContext: RequestContext
    @InjectMocks lateinit var announcement: NewsletterAnnouncement

    @Before
    fun setUp() {
        val toggles = createToggles(follower = true)
        Mockito.`when`(requestContext.toggles()).thenReturn(toggles)
    }


    @Test
    fun `show when newsletter delivery schedule not set`() {
        val user = UserModel(blog=true, newsletterDeliveryDayOfWeek = -1)
        Mockito.`when`(requestContext.currentUser()).thenReturn(user)

        assertTrue(announcement.show(PageName.BLOG))
    }

    @Test
    fun `do not show when newsletter delivery schedule set`() {
        val user = UserModel(blog=true, newsletterDeliveryDayOfWeek = 3)
        Mockito.`when`(requestContext.currentUser()).thenReturn(user)

        assertFalse(announcement.show(PageName.BLOG))
    }

    @Test
    fun `do not show show when follow toggle is OFF`() {
        val user = UserModel(blog=true, newsletterDeliveryDayOfWeek = -1)
        Mockito.`when`(requestContext.currentUser()).thenReturn(user)

        val toggles = createToggles(follower = false)
        Mockito.`when`(requestContext.toggles()).thenReturn(toggles)

        assertFalse(announcement.show(PageName.BLOG))
    }


    @Test
    fun name() {
        assertEquals("newsletter", announcement.name())
    }

    @Test
    fun actionUrl() {
        assertEquals("/me/settings?highlight=newsletter-container#newsletter", announcement.actionUrl())
    }

    @Test
    fun iconUrl() {
        assertEquals("/assets/wutsi/img/newsletter.png", announcement.iconUrl())
    }

    @Test
    fun cookieMaxAge() {
        assertEquals(CookieHelper.ONE_DAY_SECONDS, announcement.cookieMaxAge())
    }

    @Test
    fun autoHide() {
        assertFalse(announcement.autoHide())
    }

    @Test
    fun deley() {
        assertEquals(10000, announcement.delay())
    }

    private fun createToggles(follower: Boolean=true): Toggles {
        val toggles = Toggles()
        toggles.follow = follower
        return toggles
    }
}
