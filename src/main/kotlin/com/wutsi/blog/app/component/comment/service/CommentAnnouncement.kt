package com.wutsi.blog.app.component.comment.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.announcement.service.impl.AbstractAnnouncement
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.client.comment.SearchCommentRequest
import org.springframework.stereotype.Service

@Service
class CommentAnnouncement(
    requestContext: RequestContext,
    private val comments: CommentService,
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
        val counts = comments.count(
            SearchCommentRequest(
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

    override fun name() = "comment"

    override fun description(): String {
        return requestContext.getMessage(
            key = "announcement.comment.message",
            args = arrayOf(total!!)
        )
    }

    override fun actionUrl() = "${story?.slug}?comment=1"

    override fun iconUrl() = "/assets/wutsi/img/comment.png"

    override fun cookieMaxAge(): Int = CookieHelper.ONE_HOUR_SECONDS
}
