package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.editor.service.EJSFilterSet
import com.wutsi.blog.app.page.schemas.StorySchemasGenerator
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.util.PageName
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletResponse

@Controller
class ReadController(
        private val schemas: StorySchemasGenerator,
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

    override fun generateSchemas(story: StoryModel) = schemas.generate(story)

    override fun showNotificationOptIn(): Boolean = true

    @GetMapping("/read/{id}/{title}")
    fun read(
            @PathVariable id: Long,
            @PathVariable title: String,
            @RequestParam(required = false) comment: String? = null,
            model: Model,
            response: HttpServletResponse): String {
        return read(id, comment, model, response)
    }

    @GetMapping("/read/{id}")
    fun read(
            @PathVariable id: Long,
            @RequestParam(required = false) comment: String? = null,
            model: Model,
            response: HttpServletResponse
    ): String {
        val story = loadPage(id, model)

        val fmt = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        val maxAge = 3600   // 1jr
        val lastModified = story.modificationDateTimeAsDate
        val expires = DateUtils.addSeconds(lastModified, maxAge)

        response.setHeader("Last-Modified", fmt.format(lastModified))
        response.setHeader("Expires", fmt.format(expires))
        response.setHeader("Cache-Control", "public, max-age=$maxAge")

        return "page/story/read"
    }
}
