package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.editor.service.EJSFilterSet
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import org.jsoup.Jsoup
import org.springframework.ui.Model
import java.io.StringWriter

abstract class AbstractStoryReadController(
        private val ejsJsonReader: EJSJsonReader,
        private val ejsHtmlWriter: EJSHtmlWriter,
        private val ejsFilters: EJSFilterSet,
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryController(service, requestContext) {

    protected fun loadPage(id: Long, model: Model): StoryModel {
        val story = getStory(id)
        model.addAttribute("story", story)

        val page = toPage(story)
        model.addAttribute(ModelAttributeName.PAGE, page)

        loadContent(story, model)
        return story
    }

    protected open fun generateSchemas(story: StoryModel): String? = null

    protected open fun showNotificationOptIn(): Boolean = false

    private fun loadContent(story: StoryModel, model: Model) {
        if (story.content == null){
            return
        }

        val ejs = ejsJsonReader.read(story.content)
        val html = StringWriter()
        ejsHtmlWriter.write(ejs, html)
        model.addAttribute("html", filter(html.toString()))
        model.addAttribute("hasTwitterEmbed", hasTwitterEmbed(ejs))
        model.addAttribute("hasCode", hasCode(ejs))
        model.addAttribute("hasRaw", hasRaw(ejs))
    }

    private fun hasTwitterEmbed(doc: EJSDocument) = doc
            .blocks
            .find { it.type == BlockType.embed && it.data.service == "twitter" } != null

    private fun hasCode(doc: EJSDocument) = doc
            .blocks
            .find { it.type == BlockType.code } != null

    private fun hasRaw(doc: EJSDocument) = doc
            .blocks
            .find { it.type == BlockType.raw } != null

    private fun filter(html: String): String {
        val doc = Jsoup.parse(html)
        ejsFilters.filter(doc)
        return doc.html()
    }

    protected fun toPage(story: StoryModel)= createPage(
            name = pageName(),
            title = story.title,
            description = story.summary,
            type = "article",
            url = url(story),
            imageUrl = story.thumbnailUrl,
            author = story.user.fullName,
            publishedTime = story.publishedDateTimeISO8601,
            modifiedTime = story.modificationDateTimeISO8601,
            tags = story.tags.map { it.name },
            twitterUserId = story.user.twitterId,
            canonicalUrl = story.sourceUrl,
            schemas = generateSchemas(story),
            showNotificationOptIn = showNotificationOptIn()
    )

}
