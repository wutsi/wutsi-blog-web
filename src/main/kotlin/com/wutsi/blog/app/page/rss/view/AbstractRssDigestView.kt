package com.wutsi.blog.app.page.rss.view

import com.rometools.rome.feed.rss.Channel
import com.rometools.rome.feed.rss.Description
import com.rometools.rome.feed.rss.Item
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.apache.commons.lang.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.servlet.view.feed.AbstractRssFeedView
import java.util.Calendar
import java.util.Date
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


abstract class AbstractRssDigestView(private val service: StoryService): AbstractRssFeedView() {

    @Value("\${wutsi.base-url}")
    protected lateinit var baseUrl: String

    abstract protected fun getTitle(): String

    abstract protected fun getDescription(): String

    protected abstract fun findStories(): List<StoryModel>

    override fun buildFeedMetadata(model: MutableMap<String, Any>?, feed: Channel?, request: HttpServletRequest?) {
        feed?.title = getTitle()
        feed?.description = getDescription()
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
            item.title = it.title
            item.link = "${baseUrl}${it.slug}"
            item.description = description
            item.pubDate = it.publishedDateTimeAsDate
            items.add(item)
        }
        return items
    }

    protected fun yesterday(): Date {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        return DateUtils.addDays(today.time, -1)
    }

    protected fun findStories(startDate: Date, endDate: Date): List<StoryModel> {
        return service.search(SearchStoryRequest(
                status = StoryStatus.published,
                live = true,
                limit = 30,
                sortBy = StorySortStrategy.published,
                publishedStartDate = endDate,
                publishedEndDate = startDate
        ))
    }
}
