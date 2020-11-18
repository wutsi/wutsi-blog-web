package com.wutsi.blog.app.page.blog.view

import com.wutsi.blog.app.page.rss.view.AbstractRssDigestView
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.apache.commons.lang.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest


class BlogRssView(
        private val user: UserModel,
        private val service: StoryService,
        private val baseUrl: String
): AbstractRssDigestView(service, baseUrl) {

    override fun getTitle() = "${user.fullName}(@${user.name}) RSS Feed"

    override fun getDescription() = if (user.biography == null) "" else user.biography

    override fun getLink(): String = "$baseUrl/@/${user.name}"

    override fun findStories(request: HttpServletRequest): List<StoryModel>
        = service.search(SearchStoryRequest(
                userIds = listOf(user.id),
                status = StoryStatus.published,
                live = true,
                limit = 10,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending
        ))
}
