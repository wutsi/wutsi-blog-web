package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.editor.service.EJSFilterSet
import com.wutsi.blog.app.page.story.service.RecommendationService
import com.wutsi.blog.app.page.story.service.StorySchemasGenerator
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.security.model.Permission
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
        schemas: StorySchemasGenerator,
        ejsJsonReader: EJSJsonReader,
        ejsHtmlWriter: EJSHtmlWriter,
        ejsFilters: EJSFilterSet,
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryReadController(ejsJsonReader, ejsHtmlWriter, ejsFilters, schemas, service, requestContext) {

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
    fun recommend(
            @PathVariable id: Long,
            @RequestParam(required = false, defaultValue = "summary") layout: String = "summary",
            model: Model
    ): String {
        val stories = recommendations
                .search(id)
                .take(3)

        model.addAttribute("layout", layout)
        model.addAttribute("stories", stories)
        return "page/story/recommend"
    }
}
