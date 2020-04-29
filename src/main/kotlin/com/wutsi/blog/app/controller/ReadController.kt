package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.editorjs.EJSFilterSet
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.blog.app.util.PageName
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.io.StringWriter

@Controller
class ReadController(
        private val storyService: StoryService,
        private val ejsJsonReader: EJSJsonReader,
        private val ejsHtmlWriter: EJSHtmlWriter,
        private val ejsFilters: EJSFilterSet,
        requestContext: RequestContext
): AbstractPageController(requestContext) {

    @Value("\${wutsi.oauth.google.client-id}")
    protected lateinit var googleClientId: String


    override fun pageName() = PageName.READ

    override fun shouldBeIndexedByBots() = true


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
        val story = get(id, preview)
        model.addAttribute("story", story)

        val html = filter(toHtml(story))
        model.addAttribute("html", html)

        model.addAttribute(ModelAttributeName.PAGE, toPage(story))
        model.addAttribute("preview", preview)

        activateGoogleOneTap(model)
        return "page/read"
    }

    private fun activateGoogleOneTap(model: Model) {
        if (!requestContext.toggles().googleOneTapSignIn){
            return
        }

        val accessToken = requestContext.accessToken()
        if (accessToken == null){
            model.addAttribute("googleOneTap", true)
        }
    }

    private fun get(id: Long, preview: Boolean): StoryModel {
        val story = storyService.get(id)

        if (preview) {
            checkOwnership(story)   // Only owner can preview a story
        } else {
            checkPublished(story)
        }

        return story
    }

    private fun toHtml(story: StoryModel): String {
        if (story.content == null){
            return ""
        }

        val ejs = ejsJsonReader.read(story.content)
        val html = StringWriter()
        ejsHtmlWriter.write(ejs, html)
        return html.toString()
    }

    private fun filter(html: String): String {
        val doc = Jsoup.parse(html)
        ejsFilters.filter(doc)
        return doc.html()
    }

    private fun toPage(story: StoryModel)= PageModel(
            name = pageName(),
            title = story.title!!,
            description = story.summary!!,
            type = "article",
            url = url(story),
            imageUrl = story.thumbnailUrl,
            author = story.user.fullName,
            publishedTime = story.publishedDateTimeISO8601,
            modifiedTime = story.modificationDateTimeISO8601,
            baseUrl = baseUrl,
            assetUrl = assetUrl,
            robots = robots(),
            tags = story.tags.map { it.name },
            twitterUserId = story.user.accounts.find { it.provider == "twitter" }?.providerUserId,
            googleAnalyticsCode = this.googleAnalyticsCode,
            googleClientId = this.googleClientId,
            canonicalUrl = story.sourceUrl
    )

}
