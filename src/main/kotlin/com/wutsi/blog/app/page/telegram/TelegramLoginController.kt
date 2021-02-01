package com.wutsi.blog.app.page.telegram

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping()
@ConditionalOnProperty(value = ["wutsi.toggles.channel-telegram"], havingValue = "true")
class TelegramLoginController {
    @GetMapping("/login/telegram")
    fun login(): String {
        return "redirect:/telegram"
    }
}
