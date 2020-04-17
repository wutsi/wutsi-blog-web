package com.wutsi.blog.app.controller.story

import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class StoryController(
        private val service: StoryService
) {

    @GetMapping("/story/cards")
    fun index(
            @RequestParam(required = false) userId: Long? = null,
            @RequestParam(required = false, defaultValue = "20") limit: Int,
            @RequestParam(required = false, defaultValue = "0") offset: Int,
            @RequestParam(required = false, defaultValue = "false") showCreatePanel: Boolean,
            model: Model
    ): String {
        val stories = service.search(SearchStoryRequest(
                userId = userId,
                limit = limit,
                offset = offset,
                status = StoryStatus.published,
                sortBy = StorySortStrategy.published
        ))

        model.addAttribute("limit", limit)
        model.addAttribute("offset", limit)
        model.addAttribute("stories", stories)
        model.addAttribute("showCreatePanel", showCreatePanel)
        return "/page/story/cards"
    }
}
