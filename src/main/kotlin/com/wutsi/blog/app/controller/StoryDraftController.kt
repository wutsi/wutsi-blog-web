package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/story/draft")
class StoryDraftController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun page() = PageName.STORY_DRAFT

    @GetMapping()
    fun index(
            @RequestParam(defaultValue = "20") limit:Int,
            @RequestParam(defaultValue = "0") offset:Int,
            model: Model
    ): String {
        val stories = service.drafts(limit, offset)

        model.addAttribute("stories", stories)
        return "page/story/draft"
    }
}
