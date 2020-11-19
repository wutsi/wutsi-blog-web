package com.wutsi.blog.app.page.home

import com.wutsi.blog.app.page.blog.view.BlogRssView
import com.wutsi.blog.app.page.home.view.HomeRssView
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.service.StoryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeRssController(
        private val storyService: StoryService,
        @Value("\${wutsi.base-url}") private val baseUrl: String
) {
    @GetMapping("/rss")
    fun index(): HomeRssView {
        return HomeRssView(storyService, baseUrl)
    }
}
