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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.regex.Pattern

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
        private val ID_PATTERN = Pattern.compile("/read/([0-9]+).*")
    }

    override fun pageName() = PageName.FOLLOW

    @GetMapping("/@/{username}/follow")
    fun add(
        @PathVariable username: String,
        @RequestParam(required = false) `return`: String? = null,
        @RequestParam(required = false) page: String? = null,
        @RequestParam(required = false) hitId: String? = null
    ): String {
        val user = userService.get(username)
        try {
            service.follow(user.id)
            track(
                hitId = hitId,
                page = page,
                returnUrl = `return`
            )
        } catch (ex: Exception) {
            LOGGER.error("${requestContext.currentUser()?.id} is not able to follow $username", ex)
        }
        return if (`return` == null) "redirect:${user.slug}" else "redirect:$`return`"
    }

    private fun track(hitId: String?, page: String?, returnUrl: String?) {
        trackService.push(
            PushTrackForm(
                time = System.currentTimeMillis(),
                url = requestContext.request.getHeader("Referer"),
                event = "follow",
                hid = hitId,
                page = page,
                pid = returnUrl?.let { extractStoryId(it) } ?: null
            )
        )
    }

    fun extractStoryId(url: String): String? {
        val matcher = ID_PATTERN.matcher(url)
        while (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }
}
