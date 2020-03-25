package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun page() = PageName.HOME

    @GetMapping()
    fun index(model: Model): String {
        val stories = service.search(SearchStoryRequest(
                status = StoryStatus.published,
                sortBy = StorySortStrategy.published
        ))
        model.addAttribute("stories", stories)
        return "page/home"
    }
}
