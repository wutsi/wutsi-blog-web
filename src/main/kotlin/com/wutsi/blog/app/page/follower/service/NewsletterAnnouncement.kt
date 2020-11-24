package com.wutsi.blog.app.page.follower.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.announcement.service.impl.AbstractAnnouncement
import com.wutsi.blog.app.component.like.service.LikeService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.comment.SearchCommentRequest
import com.wutsi.blog.client.like.SearchLikeRequest
import org.springframework.stereotype.Service


@Service
class NewsletterAnnouncement(
        requestContext: RequestContext
): AbstractAnnouncement(requestContext) {

    override fun show(page: String): Boolean {
        val author = requestContext.currentUser()
        return requestContext.toggles().follow
                && author?.blog == true
                && PageName.BLOG.equals(page)
                && author.newsletterDeliveryDayOfWeek <= 0
    }

    override fun name() = "newsletter"

    override fun description(): String {
        return requestContext.getMessage(
                key = "announcement.newsletter.message",
                args = arrayOf(requestContext.currentUser()!!.fullName)
        )
    }

    override fun actionUrl() =  "/me/settings?highlight=newsletter-container#newsletter"

    override fun iconUrl() = "/assets/wutsi/img/newsletter.png"

    override fun cookieMaxAge(): Int = CookieHelper.ONE_DAY_SECONDS

    override fun autoHide(): Boolean = false
}
