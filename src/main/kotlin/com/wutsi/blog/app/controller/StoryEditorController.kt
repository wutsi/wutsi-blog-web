package com.wutsi.blog.app.controller

import com.wutsi.blog.app.editor.StoryEditor
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class StoryEditorController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun page() = PageName.STORY_EDITOR

    @GetMapping("/story/editor")
    fun create(model: Model): String {
        model.addAttribute("storyId", 0)
        return "page/story/editor"
    }

    @GetMapping("/story/{id}/editor")
    fun update(@PathVariable id:Long, model: Model): String {
        model.addAttribute("storyId", id)
        return "page/story/editor"
    }

    @ResponseBody
    @GetMapping("/story/{id}/editor/fetch", produces = ["application/json"])
    fun fetch(@PathVariable id:Long): StoryModel {
        return service.get(id)
    }

    @ResponseBody
    @GetMapping("/story/editor/save", produces = ["application/json"], consumes = ["application/json"])
    fun save(@ModelAttribute editor: StoryEditor): StoryEditor {
        return service.save(editor)
    }
}
