package com.wutsi.blog.app.controller

import com.wutsi.blog.app.editor.StoryEditor
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/story/editor")
class StoryEditorController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun page() = PageName.NEW_STORY

    @GetMapping()
    fun index(model: Model): String {
        return "page/story/editor"
    }

    @ResponseBody
    @GetMapping("/save", produces = ["application/json"], consumes = ["application/json"])
    fun save(@ModelAttribute editor: StoryEditor): StoryEditor {
        return service.save(editor)
    }
}
