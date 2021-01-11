package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping
class FollowController(
    private val service: FollowerService,
    private val userService: UserService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(FollowerService::class.java)
    }

    override fun pageName() = PageName.FOLLOW

    @GetMapping("/@/{username}/follow")
    fun add(
        @PathVariable username: String,
        @RequestParam(required = false) `return`: String? = null
    ): String {
        val user = userService.get(username)
        try {
            service.follow(user.id)
        } catch (ex: Exception) {
            LOGGER.error("${requestContext.currentUser()?.id} is not able to follow $username", ex)
        }
        return if (`return` == null) "redirect:${user.slug}" else "redirect:$`return`"
    }
}
