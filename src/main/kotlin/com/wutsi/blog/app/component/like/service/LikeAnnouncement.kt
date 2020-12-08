package com.wutsi.blog.app.component.like.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.announcement.service.impl.AbstractAnnouncement
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.client.like.SearchLikeRequest
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service

@Service
@Scope("prototype")
class LikeAnnouncement(
    requestContext: RequestContext,
    private val likes: LikeService,
    private val stories: StoryService
) : AbstractAnnouncement(requestContext) {
    private var total: String? = null
    private var story: StoryModel? = null

    override fun show(page: String): Boolean {
        // User
        val author = requestContext.currentUser()
        if (author == null) {
            return false
        }

        // Last view
        val lastViewDate = requestContext.lastViewDate()
            ?: return false

        // Count
        val counts = likes.count(
            SearchLikeRequest(
                authorId = author.id,
                since = lastViewDate
            )
        ).sortedByDescending { it.value }
        if (counts.isEmpty()) {
            return false
        }

        // Get data
        this.total = counts[0].valueText
        this.story = stories.get(counts[0].storyId)
        return true
    }

    override fun description(): String {
        return requestContext.getMessage(
            key = "announcement.like.message",
            args = arrayOf(total!!)
        )
    }

    override fun name() = "like"

    override fun actionUrl() = story?.slug

    override fun iconUrl() = "/assets/wutsi/img/like.png"

    override fun cookieMaxAge(): Int = CookieHelper.ONE_HOUR_SECONDS
}
