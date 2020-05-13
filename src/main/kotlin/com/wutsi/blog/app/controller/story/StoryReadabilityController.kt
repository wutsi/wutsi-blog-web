package com.wutsi.blog.app.controller.story

import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StoryReadabilityController(
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.STORY_READABILITY

    override fun requiredPermissions() = listOf(Permission.editor)

    @GetMapping("/me/story/{id}/readability")
    fun index(@PathVariable id:Long, model: Model): String {
        val story = getStory(id)
        model.addAttribute("story", story)

        val readability = service.readability(id)
        model.addAttribute("readability", readability)

        model.addAttribute("canPublish", story.readabilityScore > readability.scoreThreshold)

        return "page/story/readability"
    }
}
