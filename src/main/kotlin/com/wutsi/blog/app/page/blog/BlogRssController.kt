package com.wutsi.blog.app.page.blog

import com.wutsi.blog.app.page.blog.view.BlogRssView
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.service.StoryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BlogRssController(
        private val userService: UserService,
        private val storyService: StoryService,
        @Value("\${wutsi.base-url}") private val baseUrl: String
) {
    @GetMapping("/@/{name}/rss")
    fun index(@PathVariable name: String): BlogRssView {
        val user = userService.get(name)
        return BlogRssView(user, storyService, baseUrl)
    }
}
