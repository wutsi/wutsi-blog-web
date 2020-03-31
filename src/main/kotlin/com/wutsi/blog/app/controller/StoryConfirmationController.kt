package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.core.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StoryConfirmationController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryConfirmationController::class.java)
    }

    override fun pageName() = PageName.STORY_CONFIRMATION

    @GetMapping("/story/{id}/confirmation")
    fun index(@PathVariable id:Long, model: Model): String {
        val story = service.get(id)
        if (!story.published){
            LOGGER.info("Story#${id} not published. status=${story.status}")
            throw NotFoundException("story_not_published")
        }
        model.addAttribute("story", story)

        val totalPublished = service.count(StoryStatus.published)
        model.addAttribute("totalPublished", totalPublished)
        model.addAttribute("firstStory", totalPublished == 1)
        return "page/story/confirmation"
    }
}
