package com.wutsi.blog.app.controller.story

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StoryConfirmationController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.STORY_CONFIRMATION

    @GetMapping("/me/story/{id}/confirmation")
    fun index(@PathVariable id:Long, model: Model): String {
        val story = get(id)
        model.addAttribute("story", story)

        val totalPublished = service.count(StoryStatus.published)
        model.addAttribute("totalPublished", totalPublished)
        model.addAttribute("firstStory", totalPublished == 1)
        return "page/story/confirmation"
    }

    private fun get(id: Long): StoryModel {
        val story = service.get(id)
        checkPublished(story)
        checkOwnership(story)

        return story
    }
}
