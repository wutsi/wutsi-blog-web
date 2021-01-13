package com.wutsi.blog.app.page.blog.service.nba

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.Toggles
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.client.channel.ChannelType.twitter
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TwitterNextActionTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @InjectMocks
    private lateinit var nba: TwitterNextAction

    @Test
    fun `return null when toggle off`() {
        setupToggle(false)

        Assert.assertNull(nba.get(UserModel(), channels(true)))
    }

    @Test
    fun `return null when toggle ON and channel connected`() {
        setupToggle(true)

        Assert.assertNull(nba.get(UserModel(), channels(true)))
    }

    @Test
    fun `return action when channel not connected`() {
        setupToggle(true)

        testAction(channels(false))
    }

    @Test
    fun `return action when no channel`() {
        setupToggle(true)

        testAction(emptyList<ChannelModel>())
    }

    private fun testAction(channels: List<ChannelModel>) {
        doReturn("This is title").whenever(requestContext).getMessage(any(), eq(null), eq(null), eq(null))

        val result = nba.get(UserModel(), channels)

        Assert.assertEquals("twitter", result?.name)
        Assert.assertEquals("/assets/wutsi/img/social/twitter.png", result?.iconUrl)
        Assert.assertEquals("/me/settings?highlight=channels-container#channels", result?.url)
        Assert.assertEquals("This is title", result?.title)
    }

    private fun setupToggle(enabled: Boolean) {
        val toggles = Toggles()
        toggles.channelTwitter = enabled

        doReturn(toggles).whenever(requestContext).toggles()
    }

    private fun channels(connected: Boolean): List<ChannelModel> {
        val model = ChannelModel(type = twitter, connected = connected)
        return listOf(model)
    }
}
