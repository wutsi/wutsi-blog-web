package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.track.model.PushTrackForm
import com.wutsi.blog.app.page.track.service.TrackService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping
class FollowController(
    private val service: FollowerService,
    private val userService: UserService,
    private val trackService: TrackService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(FollowerService::class.java)
    }

    override fun pageName() = PageName.FOLLOW

    /**
     * NOTE: This endpoint requires login. This is the default page for following a blog.
     */
    @GetMapping("/@/{username}/follow")
    fun follow(
        @PathVariable username: String,
        @RequestParam(required = false) `return`: String? = null,
        @RequestParam(required = false) page: String? = null,
        @RequestParam(required = false) hitId: String? = null,
        @RequestParam(required = false) storyId: String? = null
    ): String {
        val user = userService.get(username)
        try {
            follow(user.id)
        } catch (ex: Exception) {
            LOGGER.error("${requestContext.currentUser()?.id} is not able to follow $username", ex)
        }
        return if (`return` == null) "redirect:${user.slug}" else "redirect:$`return`"
    }

    /**
     * NOTE: This endpoint DOES NOT requires login. This endpoint is called by the follow-popup to
     * subscribe a user to a blog in one tap
     */
    @PostMapping("/follow")
    @ResponseBody
    fun follow(
        @RequestParam userId: Long,
        @RequestParam(required = false) page: String? = null,
        @RequestParam(required = false) hitId: String? = null,
        @RequestParam(required = false) storyId: String? = null
    ): Map<String, String> {
        val id = service.follow(userId)
        track(
            hitId = hitId,
            page = page,
            storyId = storyId
        )
        return mapOf("id" to id.toString())
    }

    private fun track(hitId: String?, page: String?, storyId: String?) {
        trackService.push(
            PushTrackForm(
                time = System.currentTimeMillis(),
                url = requestContext.request.getHeader("Referer"),
                event = "follow",
                hid = hitId,
                page = page,
                pid = storyId
            )
        )
    }
}
