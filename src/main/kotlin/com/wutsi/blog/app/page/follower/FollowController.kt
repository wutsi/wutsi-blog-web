package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder

@Controller
@RequestMapping("/follow")
class FollowController(
        private val service: FollowerService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    companion object{
        private val LOGGER = LoggerFactory.getLogger(FollowerService::class.java)
    }

    override fun pageName() = PageName.FOLLOW

    @GetMapping()
    fun add(
            @RequestParam userId: Long,
            @RequestParam(required = false) `return`: String? = null
    ): String {
        try {
            service.follow(userId)
        } catch(ex: Exception){
            LOGGER.error("${requestContext.currentUser()?.id} is not able to follow ${userId}", ex)
        }
        return "redirect:$`return`"
    }
}
