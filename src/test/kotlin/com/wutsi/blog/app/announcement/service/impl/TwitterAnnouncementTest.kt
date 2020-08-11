package com.wutsi.blog.app.announcement.service.impl

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.Toggles
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.ChannelType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TwitterAnnouncementTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @Mock
    private lateinit var channels: ChannelService

    @InjectMocks
    lateinit var announcement: TwitterAnnouncement

    @Test
    fun supports() {
        assertTrue(announcement.supports(PageName.HOME))
        assertTrue(announcement.supports(PageName.BLOG))
        assertFalse(announcement.supports(PageName.READ))
        assertFalse(announcement.supports("xxx"))
    }

    @Test
    fun `show for blogger`() {
        val user = UserModel(blog=true)
        val toggles = createToggles()
        `when`(requestContext.currentUser()).thenReturn(user)
        `when`(requestContext.toggles()).thenReturn(toggles)

        val channel1 = createChannel(ChannelType.twitter, false)
        val channel2 = createChannel(ChannelType.facebook, false)
        `when`(channels.all()).thenReturn(arrayListOf(channel1 ,channel2))

        assertTrue(announcement.show())
    }

    @Test
    fun `never show for non-blogger`() {
        val user = UserModel(blog=false)
        val toggles = createToggles()
        `when`(requestContext.currentUser()).thenReturn(user)
        `when`(requestContext.toggles()).thenReturn(toggles)

        val channel1 = createChannel(ChannelType.twitter, false)
        val channel2 = createChannel(ChannelType.facebook, false)
        `when`(channels.all()).thenReturn(arrayListOf(channel1 ,channel2))

        assertFalse(announcement.show())
    }

    @Test
    fun `never show for anonymous`() {
        val toggles = createToggles()
        `when`(requestContext.currentUser()).thenReturn(null)
        `when`(requestContext.toggles()).thenReturn(toggles)

        val channel1 = createChannel(ChannelType.twitter, false)
        val channel2 = createChannel(ChannelType.facebook, false)
        `when`(channels.all()).thenReturn(arrayListOf(channel1 ,channel2))

        assertFalse(announcement.show())
    }

    @Test
    fun `never show when connected`() {
        val user = UserModel(blog=false)
        val toggles = createToggles()
        `when`(requestContext.currentUser()).thenReturn(user)
        `when`(requestContext.toggles()).thenReturn(toggles)

        val channel1 = createChannel(ChannelType.twitter, true)
        val channel2 = createChannel(ChannelType.facebook, false)
        `when`(channels.all()).thenReturn(arrayListOf(channel1 ,channel2))

        assertFalse(announcement.show())
    }


    @Test
    fun `never show when channel toggle OFF`() {
        val user = UserModel(blog=false)
        val toggles = createToggles(channel = false)
        `when`(requestContext.toggles()).thenReturn(toggles)

        assertFalse(announcement.show())
    }


    @Test
    fun `never show when channel-twitter toggle OFF`() {
        val user = UserModel(blog=false)
        val toggles = createToggles(channelTwitter = false)
        `when`(requestContext.toggles()).thenReturn(toggles)

        assertFalse(announcement.show())
    }

    @Test
    fun name() {
        assertEquals("twitter", announcement.name())
    }

    @Test
    fun actionUrl() {
        assertEquals("/me/channel", announcement.actionUrl())
    }

    private fun createToggles(channel: Boolean=true, channelTwitter: Boolean = true): Toggles {
        val toggles = Toggles()
        toggles.channel = channel
        toggles.channelTwitter = channelTwitter
        return toggles
    }

    private fun createChannel(type: ChannelType, connected: Boolean) = ChannelModel(type=type, connected = connected)
}
