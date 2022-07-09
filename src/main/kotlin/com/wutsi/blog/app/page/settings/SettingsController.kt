package com.wutsi.blog.app.page.settings

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.settings.model.UserAttributeForm
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.user.SearchUserRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/me/settings")
class SettingsController(
    private val userService: UserService,
    private val followerService: FollowerService,
    private val channelService: ChannelService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS

    @GetMapping
    fun index(
        @RequestParam(required = false) highlight: String? = null,
        model: Model
    ): String {
        model.addAttribute("highlight", highlight)
        loadFollowingUsers(model)
        loadChannels(model)
        return "page/settings/index"
    }

    private fun loadFollowingUsers(model: Model) {
        val userIds = followerService.searchFollowingUserIds()
        if (userIds.isNotEmpty()) {
            model.addAttribute(
                "followingUsers",
                userService.search(
                    SearchUserRequest(
                        userIds = userIds,
                        limit = 20
                    )
                )
            )
        }
    }

    private fun loadChannels(model: Model) {
        val channels = channelService.all()
        model.addAttribute("channels", channels)
    }

    @ResponseBody
    @PostMapping(produces = ["application/json"], consumes = ["application/json"])
    fun set(@RequestBody request: UserAttributeForm): Map<String, Any?> {
        try {
            userService.set(request)
            return mapOf("id" to requestContext.currentUser()?.id)
        } catch (ex: Exception) {
            val key = errorKey(ex)
            return mapOf(
                "id" to requestContext.currentUser()?.id,
                "error" to requestContext.getMessage(key)
            )
        }
    }

    @GetMapping("/unsubscribe")
    fun unsubscribe(@RequestParam userId: Long): String {
        followerService.unfollow(userId)
        return "redirect:/me/settings#subscriptions"
    }

    @GetMapping("/channel/create")
    fun create(
        @RequestParam id: String,
        @RequestParam accessToken: String,
        @RequestParam accessTokenSecret: String,
        @RequestParam name: String,
        @RequestParam(required = false) pictureUrl: String? = null,
        @RequestParam type: ChannelType
    ): String {
        channelService.create(id, accessToken, accessTokenSecret, name, pictureUrl, type)
        return channelRedirectUrl()
    }

    @GetMapping("/channel/connect")
    fun connect(@RequestParam type: ChannelType): String {
        return "redirect:/login/" + type.name + "?connect=1"
    }

    @GetMapping("/channel/disconnect")
    fun disconnect(@RequestParam channelId: Long): String {
        channelService.delete(channelId)
        return channelRedirectUrl()
    }

    private fun channelRedirectUrl(): String =
        "redirect:/me/settings?highlight=channels-container#channels"
}
