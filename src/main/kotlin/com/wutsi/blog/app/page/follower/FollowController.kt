package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/follow")
class FollowController(
        private val service: FollowerService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.FOLLOW

    @GetMapping()
    fun add(
            @RequestParam blogId: Long,
            @RequestParam(required = false) `return`: String? = null
    ): String {
        if (requestContext.currentUser() == null){
            return "redirect:/login?blogId=$blogId&reason=follow&return=" + `return` + "&redirect=" + `return`
        }

        service.follow(blogId)
        return "redirect:" + `return`
    }
}
