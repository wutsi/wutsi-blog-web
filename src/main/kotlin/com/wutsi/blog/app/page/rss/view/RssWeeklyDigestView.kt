package com.wutsi.blog.app.page.rss.view

import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.client.story.SortAlgorithmType
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest


@Component
class RssWeeklyDigestView(private val service: StoryService): AbstractRssDigestView(service) {

    override fun getTitle() = "Wutsi Weekly Digest"

    override fun getDescription() = "Your Weekly Digest"

    override fun findStories(request: HttpServletRequest): List<StoryModel> {
        val yesterday = yesterday()
        val storyId = getUserId(request)
        val stories = findStories(storyId, yesterday, DateUtils.addDays(yesterday, -7))
        return if (stories.isNotEmpty()){
            service.sort(stories, SortAlgorithmType.most_viewed, 24*7)
        } else {
            stories
        }
    }
}
