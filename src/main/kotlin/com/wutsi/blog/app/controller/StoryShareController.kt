package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.core.exception.NotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StoryShareController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.STORY_SHARE

    @GetMapping("/story/{id}/share")
    fun index(@PathVariable id:Long, model: Model): String {
        val story = service.get(id)
        if (!story.published){
            throw NotFoundException("story_not_published")
        }

        model.addAttribute("url", url(story))
        model.addAttribute("story", story)
        return "page/story/share"
    }
}
