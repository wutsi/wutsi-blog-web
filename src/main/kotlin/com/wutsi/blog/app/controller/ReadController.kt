package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.io.StringWriter

@Controller
class ReadController(
        private val storyService: StoryService,
        private val ejsJsonReader: EJSJsonReader,
        private val ejsHtmlWriter: EJSHtmlWriter,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun page() = PageName.STORY_EDITOR

    @GetMapping("/read/{id}")
    fun read(@PathVariable id: Long, model: Model): String {
        val story = storyService.get(id)
        model.addAttribute("story", story)

        val ejs = ejsJsonReader.read(story.content!!)
        val html = StringWriter()
        ejsHtmlWriter.write(ejs, html)
        model.addAttribute("html", html)
        return "page/read"
    }

    @GetMapping("/read/{id}/{title}")
    fun read(@PathVariable id: Long, @PathVariable title: String, model: Model): String {
        return read(id, model)
    }
}
