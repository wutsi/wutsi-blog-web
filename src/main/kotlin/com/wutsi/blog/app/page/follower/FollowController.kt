package com.wutsi.blog.app.page.follower

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class FollowController {
    @GetMapping("/@/{username}/follow")
    fun follow(
        @PathVariable username: String
    ): String {
        return "redirect:/@/$username/subscribe"
    }
}
