package com.wutsi.blog.app.service.rss

import com.rometools.rome.feed.rss.Channel
import com.rometools.rome.feed.rss.Description
import com.rometools.rome.feed.rss.Item
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.apache.commons.lang.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.servlet.view.feed.AbstractRssFeedView
import java.util.Date
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class RssWeeklyDigestView(
        private val service: StoryService,
        @Value("\${wutsi.base-url}") private val baseUrl: String
): AbstractRssFeedView() {

    override fun buildFeedMetadata(model: MutableMap<String, Any>?, feed: Channel?, request: HttpServletRequest?) {
        feed?.title = "Wutsi Weekly Digest"
        feed?.description = "Your Weekly Digest"
        feed?.link = baseUrl
    }

    override fun buildFeedItems(model: MutableMap<String, Any>?, request: HttpServletRequest?, response: HttpServletResponse?): MutableList<Item> {
        val items = mutableListOf<Item>()
        val stories = findStories()
        stories.forEach {
            val description = Description()
            description.value = it.summary

            val item = Item()
            item.author = it.user.fullName
            item.link = "${baseUrl}${it.slug}"
            item.description = description
            item.pubDate = it.publishedDateTimeAsDate
            items.add(item)
        }
        return items
    }

    private fun findStories(): List<StoryModel> {
        val now = Date()
        val stories = service.search(SearchStoryRequest(
                status = StoryStatus.published,
                live = true,
                limit = 30,
                sortBy = StorySortStrategy.published,
                publishedStartDate = DateUtils.addDays(now, -7),
                publishedEndDate = now
        ))

        return if (stories.isNotEmpty()){
            service.sort(stories, SortAlgorithmType.most_viewed, 24*7)
        } else {
            stories
        }
    }
}
