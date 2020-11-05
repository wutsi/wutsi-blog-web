package com.wutsi.blog.app.page.story

import au.com.flyingkite.mobiledetect.UAgentInfo
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
import org.springframework.context.i18n.LocaleContextHolder
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
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryReadController(ejsJsonReader, service, requestContext) {

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
            @RequestParam(required = false) translate: String? = null,
            model: Model,
            response: HttpServletResponse): String {
        return read(id, comment, translate, model, response)
    }

    @GetMapping("/read/{id}")
    fun read(
            @PathVariable id: Long,
            @RequestParam(required = false) comment: String? = null,
            @RequestParam(required = false) translate: String? = null,
            model: Model,
            response: HttpServletResponse
    ): String {
        val story = loadPage(id, model, translate)

        if (translate != null) {
            loadTranslationInfo(story, model)
        }
        return "page/story/read"
    }

    private fun loadTranslationInfo(story: StoryModel, model: Model) {
        val locale = LocaleContextHolder.getLocale()
        val lang = locale.language
        if (lang != story.language && requestContext.supports(locale)) {

            model.addAttribute("translationUrl", "${story.slug}?translate=lang")
            model.addAttribute("translationText", requestContext.getMessage(
                    key = "label.read_translation",
                    locale = locale
            ))
        }
    }
}
