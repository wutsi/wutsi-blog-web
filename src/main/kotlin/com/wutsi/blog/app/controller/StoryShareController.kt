package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StoryShareController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryShareController::class.java)
    }

    override fun page() = PageName.STORY_SHARE

    @GetMapping("/story/{id}/share")
    fun index(@PathVariable id:Long, model: Model): String {
        val story = service.get(id)
        if (!story.published){
            LOGGER.info("Story#${id} not published. status=${story.status}")
            throw NotFoundException("story_not_published")
        }
        model.addAttribute("story", story)
        return "page/story/share"
    }
}
