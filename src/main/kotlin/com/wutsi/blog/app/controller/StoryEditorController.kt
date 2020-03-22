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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/story/editor")
class StoryEditorController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun page() = PageName.STORY_EDITOR

    @GetMapping()
    fun index(
            @RequestParam(required = false) id:Long? = null,
            model: Model
    ): String {

        if (id != null) {
            model.addAttribute("storyId", id)
        } else {
            model.addAttribute("storyId", 0)
        }

        return "page/story/editor"

    }

    @ResponseBody
    @GetMapping("/fetch", produces = ["application/json"])
    fun fetch(@RequestParam id:Long): StoryModel {
        return service.get(id)
    }

    @ResponseBody
    @GetMapping("/save", produces = ["application/json"], consumes = ["application/json"])
    fun save(@ModelAttribute editor: StoryEditor): StoryEditor {
        return service.save(editor)
    }
}
