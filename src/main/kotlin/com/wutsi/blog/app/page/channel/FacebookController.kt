package com.wutsi.blog.app.page.channel

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/me/channel/facebook")
class FacebookController(
        private val service: ChannelService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.CHANNEL_FACEBOOK

    @GetMapping
    fun index(
            @RequestParam accessToken: String,
            @RequestParam name: String,
            @RequestParam pictureUrl: String
    ): String {
        val url = "/me/channel/create?" +
                "accessToken=${accessToken}" +
                "&accessTokenSecret=-" +
                "&name=${name}" +
                "&pictureUrl=${pictureUrl}" +
                "&type=" + ChannelType.facebook
        return "redirect:$url"
    }
}
