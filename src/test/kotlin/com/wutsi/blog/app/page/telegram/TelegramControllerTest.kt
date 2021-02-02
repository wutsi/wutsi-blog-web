package com.wutsi.blog.app.page.telegram

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.CreateChannelResponse
import com.wutsi.blog.client.telegram.CheckBotAccessResponse
import org.junit.Test

class TelegramControllerTest : SeleniumMobileTestSupport() {

    @Test
    fun `connect with telegram group`() {
        val response = CheckBotAccessResponse(chatId = "yo", chatName = "Man")
        doReturn(response).whenever(telegramApi).checkAccess(any())
        doReturn(CreateChannelResponse(111)).whenever(channelApi).create(any())

        // When
        login()
        driver.get("$url/telegram")
        assertCurrentPageIs(PageName.TELEGRAM)

        // Then
        input("#telegram-chat-title", "test")
        input("#telegram-username", "ray.sponsible")
        click("#btn-telegram-connect")

        Thread.sleep(2000)
        assertCurrentPageIs(PageName.SETTINGS)
    }
}
