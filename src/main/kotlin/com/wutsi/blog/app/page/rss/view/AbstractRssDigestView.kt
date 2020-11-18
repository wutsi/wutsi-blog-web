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


abstract class AbstractRssDigestView(
        private val service: StoryService,
        private val baseUrl: String
): AbstractRssFeedView() {
    abstract protected fun getTitle(): String

    abstract protected fun getDescription(): String

    open protected fun getLink(): String = baseUrl

    protected abstract fun findStories(request: HttpServletRequest): List<StoryModel>

    override fun buildFeedMetadata(model: MutableMap<String, Any>?, feed: Channel?, request: HttpServletRequest) {
        feed?.title = getTitle()
        feed?.description = getDescription()
        feed?.link = getLink()
    }

    override fun buildFeedItems(
            model: MutableMap<String, Any>?,
            request: HttpServletRequest,
            response: HttpServletResponse?
    ): MutableList<Item> {
        val items = mutableListOf<Item>()
        val stories = findStories(request)
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

    protected fun findStories(userId: Long?, startDate: Date, endDate: Date): List<StoryModel> {
        return service.search(SearchStoryRequest(
                userIds = if (userId == null) emptyList() else listOf(userId),
                status = StoryStatus.published,
                live = true,
                limit = 30,
                sortBy = StorySortStrategy.published,
                publishedStartDate = endDate,
                publishedEndDate = startDate
        ))
    }

    protected fun getUserId(request: HttpServletRequest): Long? {
        try {
            val userId = request.getParameter("userId")
                    ?: return null

            return userId.toLong()
        } catch (ex: Exception){
            return null
        }
    }

}
