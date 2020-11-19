package com.wutsi.blog.app.page.home.view

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
import java.util.Date
import javax.servlet.http.HttpServletRequest


class HomeRssView(
        private val service: StoryService,
        private val baseUrl: String
): AbstractRssDigestView(service, baseUrl) {

    override fun getTitle() = "Wutsi RSS Feed"

    override fun getDescription() = "Wutsi RSS Feed"

    override fun getLink(): String = baseUrl

    override fun findStories(request: HttpServletRequest): List<StoryModel>
        = service.search(SearchStoryRequest(
                status = StoryStatus.published,
                live = true,
                limit = 10,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending
        ))
}
