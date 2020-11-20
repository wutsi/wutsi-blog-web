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
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryController(service, requestContext) {

    protected fun loadPage(id: Long, model: Model, language: String? = null): StoryModel {
        val story = getStory(id, language)
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

        val html = service.generateHtmlContent(story)
        model.addAttribute("html", html)

        val ejs = ejsJsonReader.read(story.content)
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

    protected fun toPage(story: StoryModel)= createPage(
            name = pageName(),
            title = "${story.title} | $siteDisplayName",
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
