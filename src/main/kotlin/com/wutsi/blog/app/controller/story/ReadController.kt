package com.wutsi.blog.app.controller.story

import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.service.RecommendationService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.editorjs.EJSFilterSet
import com.wutsi.blog.app.util.PageName
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ReadController(
        private val recommendations: RecommendationService,
        ejsJsonReader: EJSJsonReader,
        ejsHtmlWriter: EJSHtmlWriter,
        ejsFilters: EJSFilterSet,
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryReadController(ejsJsonReader, ejsHtmlWriter, ejsFilters, service, requestContext) {

    override fun pageName() = PageName.READ

    override fun requiredPermissions() = listOf(Permission.reader)

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true


    @GetMapping("/read/{id}/{title}")
    fun read(@PathVariable id: Long, @PathVariable title: String, model: Model): String {
        return read(id, false, model)
    }

    @GetMapping("/read/{id}")
    fun read(
            @PathVariable id: Long,
            @RequestParam(required = false, defaultValue = "false") preview: Boolean = false,
            model: Model
    ): String {
        loadPage(id, model)
        return "page/story/read"
    }

    @GetMapping("/read/recommend/{id}")
    fun recommend(@PathVariable id: Long, model: Model): String {
        val stories = recommendations.search(id)
        model.addAttribute("stories", stories)
        return "page/story/recommend"
    }
}
