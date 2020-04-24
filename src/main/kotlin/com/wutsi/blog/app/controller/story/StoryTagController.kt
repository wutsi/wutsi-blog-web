package com.wutsi.blog.app.controller.story

import com.wutsi.blog.app.controller.AbstractPageController
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
import org.springframework.web.bind.annotation.RequestParam

@Controller
class StoryTagController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.STORY_TAG

    @GetMapping("/me/story/{id}/tag")
    fun index(
            @PathVariable id:Long,
            @RequestParam(required=false) error: String? = null,
            model: Model
    ): String {
        val story = service.get(id)
        super.checkOwnership(story)

        model.addAttribute("story", story)
        model.addAttribute("error", error)
        return "page/story/tag"
    }

    @GetMapping("/me/story/tag/submit")
    fun submit(@ModelAttribute editor: PublishForm): String {
        try {
            service.publish(editor)
            return "redirect:/me/story/${editor.id}/confirmation"
        } catch (ex: ConflictException) {
            return "redirect:/me/story/${editor.id}?error=publish_error"
        }
    }
}
