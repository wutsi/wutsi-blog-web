package com.wutsi.blog.app.page.blog

import com.wutsi.blog.app.page.blog.service.NextActionSet
import com.wutsi.blog.app.page.settings.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class NextActionController(
    private val nextBestActionSet: NextActionSet,
    private val userService: UserService,
    @Value("\${wutsi.asset-url}") private val assetUrl: String
) {
    @GetMapping("/next-action")
    fun get(
        @RequestParam id: Long,
        model: Model
    ): String {
        val blog = userService.get(id)
        val action = nextBestActionSet.get(blog)
        model.addAttribute("action", action)
        model.addAttribute("assetUrl", assetUrl)

        return "page/blog/next_action.html"
    }
}
