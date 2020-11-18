package com.wutsi.blog.app.page.rss.view

import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.client.story.SortAlgorithmType
import org.apache.commons.lang.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest


@Component
class RssDailyDigestView(
        private val service: StoryService,
        @Value("\${wutsi.base-url}") private val baseUrl: String
) : AbstractRssDigestView(service, baseUrl) {

    override fun getTitle() = "Wutsi Daily Digest"

    override fun getDescription() = "Your Daily Digest"

    override fun findStories(request: HttpServletRequest): List<StoryModel> {
        val yesterday = yesterday()
        val userId = getUserId(request)
        val stories = findStories(userId, yesterday, DateUtils.addDays(yesterday, -1))
        return if (stories.isNotEmpty()){
            // Sorts
            service.sort(stories, SortAlgorithmType.most_viewed, 24*7)

            // Get more stories
            val old = findStories(userId, DateUtils.addDays(yesterday, -2), DateUtils.addDays(yesterday, -5))

            // Merge
            val result = mutableListOf<StoryModel>()
            result.addAll(stories)
            result.addAll(old)
            return result
        } else {
            stories
        }
    }
}
