package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.PublishForm
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.core.exception.ConflictException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StoryPublishController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.STORY_PUBLISH

    @GetMapping("/me/story/{id}/publish")
    fun index(@PathVariable id:Long, model: Model): String {
        val story = service.get(id)
        super.checkOwnership(story)

        model.addAttribute("story", story)
        return "page/story/publish"
    }

    @GetMapping("/me/story/publish/submit")
    fun submit(@ModelAttribute editor: PublishForm): String {
        try {
            service.publish(editor)
            return "redirect:/me/story/${editor.id}/confirmation"
        } catch (ex: ConflictException) {
            return "redirect:/editor/${editor.id}?error=publish_error"
        }
    }
}
