package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.user.SearchUserRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder

@Controller
@RequestMapping("/follow/who")
class WhoToFollowController(
        private val followerService: FollowerService,
        private val userService: UserService,
        private val requestContext: RequestContext
) {
    @GetMapping()
    fun add(
            @RequestParam(required=false, defaultValue = "3") limit: Int = 3,
            model: Model
    ): String {
        val userId = requestContext.currentUser()?.id
        val followingUserIds = followerService.searchFollowingUserIds()
        val whoToFollow = userService.search(SearchUserRequest(
                blog = true,
                limit = followingUserIds.size + 1 + 2*limit
        ))
                .filter { it.id != userId && !followingUserIds.contains(it.id) }
                .shuffled()
                .take(limit)

        model.addAttribute("whoToFollow", whoToFollow)
        return "page/follow/who"
    }
}
