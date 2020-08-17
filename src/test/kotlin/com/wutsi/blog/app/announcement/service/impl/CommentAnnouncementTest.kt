package com.wutsi.blog.app.announcement.service.impl

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.Toggles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CommentAnnouncementTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @InjectMocks
    lateinit var announcement: CommentAnnouncement

    @Test
    fun `show when comment toggle enabled`() {
        val toggles = createToggles(true)
        `when`(requestContext.toggles()).thenReturn(toggles)

        assertTrue(announcement.show())
    }

    @Test
    fun `do not show when comment toggle disabled`() {
        val toggles = createToggles(false)
        `when`(requestContext.toggles()).thenReturn(toggles)

        assertFalse(announcement.show())
    }

    @Test
    fun name() {
        assertEquals("comment", announcement.name())
    }

    @Test
    fun actionUrl() {
        assertNull(announcement.actionUrl())
    }

    private fun createToggles(comment: Boolean=true): Toggles {
        val toggles = Toggles()
        toggles.comment = comment
        return toggles
    }

}
