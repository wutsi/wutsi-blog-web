package com.wutsi.blog.app.announcement.service.impl

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.Toggles
import com.wutsi.blog.app.page.partner.service.PartnerService
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.util.PageName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WPPAnnouncementTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @Mock
    private lateinit var partners: PartnerService

    @InjectMocks
    lateinit var announcement: WPPAnnouncement

    @Test
    fun supports() {
        assertTrue(announcement.supports(PageName.HOME))
        assertTrue(announcement.supports(PageName.BLOG))
        assertFalse(announcement.supports(PageName.READ))
        assertFalse(announcement.supports("xxx"))
    }

    @Test
    fun show() {
        val user = UserModel(blog=true)
        val toggles = createToggles()
        Mockito.`when`(requestContext.currentUser()).thenReturn(user)
        Mockito.`when`(requestContext.toggles()).thenReturn(toggles)

        Mockito.`when`(partners.isPartner()).thenReturn(false)

        assertTrue(announcement.show())
    }

    @Test
    fun `never show when registered to WPP`() {
        val user = UserModel(blog=true)
        val toggles = createToggles()
        Mockito.`when`(requestContext.currentUser()).thenReturn(user)
        Mockito.`when`(requestContext.toggles()).thenReturn(toggles)

        Mockito.`when`(partners.isPartner()).thenReturn(true)

        assertFalse(announcement.show())
    }

    @Test
    fun `never show when user not blogger`() {
        val user = UserModel(blog=false)
        val toggles = createToggles()
        Mockito.`when`(requestContext.currentUser()).thenReturn(user)
        Mockito.`when`(requestContext.toggles()).thenReturn(toggles)

        assertFalse(announcement.show())
    }

    @Test
    fun `never show when user is anonymous`() {
        val toggles = createToggles()
        Mockito.`when`(requestContext.currentUser()).thenReturn(null)
        Mockito.`when`(requestContext.toggles()).thenReturn(toggles)

        assertFalse(announcement.show())
    }


    @Test
    fun `never show when user toggle is OFF`() {
        val toggles = createToggles(false)
        Mockito.`when`(requestContext.toggles()).thenReturn(toggles)

        assertFalse(announcement.show())
    }
    @Test
    fun name() {
        assertEquals("wpp", announcement.name())
    }

    @Test
    fun actionUrl() {
        assertEquals("/partner", announcement.actionUrl())
    }

    private fun createToggles(wpp: Boolean=true): Toggles {
        val toggles = Toggles()
        toggles.wpp = wpp
        return toggles
    }

}
