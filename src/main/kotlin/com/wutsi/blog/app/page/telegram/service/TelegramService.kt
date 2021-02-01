package com.wutsi.blog.app.page.telegram.service

import com.wutsi.blog.app.page.telegram.model.TelegramChatModel
import com.wutsi.blog.app.page.telegram.model.TelegramForm
import com.wutsi.blog.client.telegram.CheckBotAccessRequest
import com.wutsi.blog.client.telegram.TelegramChatType
import com.wutsi.blog.sdk.TelegramApi
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping()
@ConditionalOnProperty(value = ["wutsi.toggles.channel-telegram"], havingValue = "true")
class TelegramService(
    private val api: TelegramApi
) {
    fun connect(form: TelegramForm): TelegramChatModel {
        val response = api.checkAccess(CheckBotAccessRequest(
            username = form.username,
            chatType = TelegramChatType.valueOf(form.chatType),
            chatTitle = form.chatTitle
        ))
        return TelegramChatModel(
            id = response.chatId,
            title = response.chatName
        )
    }
}
