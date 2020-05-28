package com.wutsi.blog.app.controller.story

import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.editorjs.EJSFilterSet
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.ui.Model
import java.io.StringWriter

abstract class AbstractStoryReadController(
        private val ejsJsonReader: EJSJsonReader,
        private val ejsHtmlWriter: EJSHtmlWriter,
        private val ejsFilters: EJSFilterSet,
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryController(service, requestContext) {

    @Value("\${wutsi.oauth.google.client-id}")
    protected lateinit var googleClientId: String

    protected fun loadPage(id: Long, model: Model) {
        val story = getStory(id)
        model.addAttribute("story", story)

        val page = toPage(story)
        model.addAttribute(ModelAttributeName.PAGE, page)

        loadContent(story, model)
    }

    private fun loadContent(story: StoryModel, model: Model) {
        if (story.content == null){
            return
        }

        val ejs = ejsJsonReader.read(story.content)
        val html = StringWriter()
        ejsHtmlWriter.write(ejs, html)
        model.addAttribute("html", filter(html.toString()))
        model.addAttribute("twitterEmbed", twitterEmbed(ejs) != null)
    }

    private fun twitterEmbed(doc: EJSDocument) = doc
            .blocks
            .find { it.type == BlockType.embed && it.data.service == "twitter" }

    private fun filter(html: String): String {
        val doc = Jsoup.parse(html)
        ejsFilters.filter(doc)
        return doc.html()
    }

    protected fun toPage(story: StoryModel)= PageModel(
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
            facebookPixelCode = this.facebookPixelId,
            googleAnalyticsCode = this.googleAnalyticsCode,
            googleClientId = this.googleClientId,
            canonicalUrl = story.sourceUrl
    )

}
