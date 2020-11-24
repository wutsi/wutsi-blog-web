package com.wutsi.blog.app.page.channel

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/me/channel")
@ConditionalOnProperty(value=["wutsi.toggles.channel"], havingValue = "true")
class ChannelController(
        private val service: ChannelService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.CHANNEL

    @GetMapping
    fun index(
            @RequestParam(required=false) error: String?=null,
            model: Model
    ): String {
        val channels = service.all()
        model.addAttribute("channels", channels)
        model.addAttribute("error", error)

        return "page/channel/index"
    }

    @GetMapping("/connect")
    fun connect(
            @RequestParam type: ChannelType
    ): String {
        return "redirect:/login/" + type.name + "?connect=1"
    }

    @GetMapping("/disconnect")
    fun disconnect(
            @RequestParam channelId: Long
    ): String {
        service.delete(channelId)
        return "redirect:/me/channel"
    }


    @GetMapping("/create")
    fun create(
            @RequestParam id: String,
            @RequestParam accessToken: String,
            @RequestParam accessTokenSecret: String,
            @RequestParam name: String,
            @RequestParam pictureUrl: String,
            @RequestParam type: ChannelType
    ): String {
        service.create(id, accessToken, accessTokenSecret, name, pictureUrl, type)
        return "redirect:/me/channel"
    }
}
