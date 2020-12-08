package com.wutsi.blog.app.page.settings.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.page.settings.model.UserModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SocialLinksAnnouncementTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @InjectMocks
    lateinit var announcement: SocialLinksAnnouncement

    @Test
    fun `show for user having no social links`() {
        val user = UserModel()
        `when`(requestContext.currentUser()).thenReturn(user)

        assertFalse(announcement.show("foo"))
    }

    @Test
    fun `never show for user having social links`() {
        val user = UserModel(youtubeUrl = "https://www.youtube.com/user/foo")
        `when`(requestContext.currentUser()).thenReturn(user)

        assertFalse(announcement.show("foo"))
    }

    @Test
    fun `nerver show for user having too many login`() {
        val user = UserModel(loginCount = Announcement.MAX_LOGIN + 1)
        `when`(requestContext.currentUser()).thenReturn(user)

        assertFalse(announcement.show("foo"))
    }

    @Test
    fun `never show for anonymous`() {
        Mockito.`when`(requestContext.currentUser()).thenReturn(null)

        assertFalse(announcement.show("foo"))
    }

    @Test
    fun name() {
        assertEquals("social_links", announcement.name())
    }

    @Test
    fun actionUrl() {
        assertEquals("/me/settings", announcement.actionUrl())
    }

    @Test
    fun autoHide() {
        assertTrue(announcement.autoHide())
    }

    @Test
    fun deley() {
        assertEquals(10000, announcement.delay())
    }
}
