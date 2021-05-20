package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.monetization.service.MonetizationService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.blog.client.story.StoryAccess.PREMIUM_SUBSCRIBER
import com.wutsi.blog.client.story.StoryAccess.SUBSCRIBER
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.json.EJSJsonReader
import org.springframework.ui.Model

abstract class AbstractStoryReadController(
    private val ejsJsonReader: EJSJsonReader,
    protected val followerService: FollowerService,
    private val monetizationService: MonetizationService,

    service: StoryService,
    requestContext: RequestContext
) : AbstractStoryController(service, requestContext) {

    protected fun loadPage(id: Long, model: Model, language: String? = null): StoryModel {
        val story = getStory(id, language)
        model.addAttribute("story", story)

        val page = toPage(story)
        model.addAttribute(ModelAttributeName.PAGE, page)

        val fullAccess = if (shouldCheckAccess())
            hasFullAccess(story)
        else
            true

        loadContent(story, model, fullAccess)
        return story
    }

    protected open fun shouldCheckAccess(): Boolean = false

    protected open fun hasFullAccess(story: StoryModel): Boolean {
        val blogId = story.user.id
        if (story.access == SUBSCRIBER) {
            return followerService.isFollowing(blogId)
        } else if (story.access == PREMIUM_SUBSCRIBER && requestContext.toggles().monetization) {
            monetizationService.currentPlan(blogId)
                ?: return true // The blog has not Premium Plan
            monetizationService.currentSubscription(blogId)
                ?: return false // The current user has no subscription
        }
        return true
    }

    protected open fun generateSchemas(story: StoryModel): String? = null

    protected open fun showNotificationOptIn(): Boolean = false

    private fun loadContent(story: StoryModel, model: Model, fullAccess: Boolean) {
        if (story.content == null) {
            return
        }

        val html = service.generateHtmlContent(story, !fullAccess)
        model.addAttribute("html", html)

        val ejs = ejsJsonReader.read(story.content)
        model.addAttribute("hasTwitterEmbed", hasEmbed(ejs, "twitter"))
        model.addAttribute("hasYouTubeEmbed", hasEmbed(ejs, "youtube"))
        model.addAttribute("hasVimeoEmbed", hasEmbed(ejs, "vimeo"))
        model.addAttribute("hasCode", hasCode(ejs))
        model.addAttribute("hasRaw", hasRaw(ejs))
        model.addAttribute("fullAccess", fullAccess)
    }

    private fun hasEmbed(doc: EJSDocument, service: String) = doc
        .blocks
        .find { it.type == BlockType.embed && it.data.service == service } != null

    private fun hasCode(doc: EJSDocument) = doc
        .blocks
        .find { it.type == BlockType.code } != null

    private fun hasRaw(doc: EJSDocument) = doc
        .blocks
        .find { it.type == BlockType.raw } != null

    protected fun toPage(story: StoryModel) = createPage(
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
        showNotificationOptIn = showNotificationOptIn(),
        preloadImageUrls = story.thumbnailLargeUrl?.let { listOf(it) } ?: emptyList()
    )
}
