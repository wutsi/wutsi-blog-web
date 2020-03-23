package com.wutsi.blog.app.controller

import com.wutsi.blog.app.editor.PublishEditor
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/story/publish")
class StoryPublishController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun page() = PageName.STORY_PUBLISH

    @GetMapping()
    fun index(id:Long, model: Model): String {
        val story = service.get(id)

        model.addAttribute("story", story)
        return "page/story/publish"
    }

    @PostMapping("/submit")
    fun submit(@ModelAttribute editor: PublishEditor): String {
        service.publish(editor)
        return "redirect:/"
    }
}
