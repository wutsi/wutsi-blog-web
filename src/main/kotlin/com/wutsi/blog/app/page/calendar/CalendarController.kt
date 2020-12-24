package com.wutsi.blog.app.page.calendar

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.model.CalendarPostModel
import com.wutsi.blog.app.page.calendar.model.CalendarStoryModel
import com.wutsi.blog.app.page.calendar.model.DayOfWeekModel
import com.wutsi.blog.app.page.calendar.service.CalendarMapper
import com.wutsi.blog.app.page.calendar.service.PostService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.core.util.DateUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate

@Controller
@RequestMapping("/me/calendar")
@ConditionalOnProperty(value = ["wutsi.toggles.post"], havingValue = "true")
class CalendarController(
    private val postService: PostService,
    private val storyService: StoryService,
    private val mapper: CalendarMapper,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.CALENDAR

    @GetMapping()
    fun index(
        @RequestParam(required = false) date: String? = null,
        model: Model
    ): String {
        // Post
        val startDate = startDate(date)
        val endDate = startDate.plusDays(7)
        val posts = postService.search(startDate, endDate)
        val stories = loadStories(posts, startDate, endDate)

        // Channels
        val channels = posts.map { it.channel }.distinctBy { it.type }
        model.addAttribute("channels", channels)

        // Calendar
        var date = startDate
        var dayOfWeeks = mutableListOf<DayOfWeekModel>()
        while (date < endDate) {
            dayOfWeeks.add(mapper.toDayOfWeekModel(date, posts, stories, requestContext.currentUser()))
            date = date.plusDays(1)
        }

        model.addAttribute("dayOfWeeks", dayOfWeeks)
        model.addAttribute("currentWeek", startDate)
        model.addAttribute("nextWeek", startDate.plusDays(7))
        model.addAttribute("previousWeek", startDate.minusDays(7))
        return "page/calendar/index"
    }

    private fun startDate(date: String?): LocalDate {
        date ?: return beginningOfTheWeek()

        try {
            return beginningOfTheWeek(LocalDate.parse(date))
        } catch (ex: Exception) {
            return beginningOfTheWeek()
        }
    }

    private fun loadStories(posts: List<CalendarPostModel>, startDate: LocalDate, endDate: LocalDate): List<CalendarStoryModel> {
        val userId = requestContext.currentUser()?.id ?: return emptyList()

        // Stories from post
        var all = mutableListOf<CalendarStoryModel>()
        all.addAll(posts.map { it.story })

        // Stories to publish
        storyService.search(
            SearchStoryRequest(
                userIds = listOf(userId),
                limit = 100
            )
        ).filter { accept(it, startDate, endDate) }
            .forEach {
                val storyId = it.id
                if (all.find { it.id == storyId } == null) {
                    all.add(mapper.toCalendarStoryModel(it))
                }
            }

        // Assign colors
        val ids = all.map { it.id }.distinctBy { it }
        val colors = arrayOf(
            "orange", "blue", "darkgreen", "darkred", "magenta"
        )
        var i = 0
        val idToColor = ids.map { it to colors[i++ % colors.size] }.toMap()

        all.forEach {
            it.color = idToColor[it.id] ?: "black"
        }
        return all.distinctBy { it.id }
    }

    private fun beginningOfTheWeek(date: LocalDate? = null): LocalDate {
        var cur = date?.let { it } ?: LocalDate.now()
        while (cur.dayOfWeek != SUNDAY)
            cur = cur.plusDays(-1)
        return cur
    }

    private fun accept(story: StoryModel, startDate: LocalDate, endDate: LocalDate): Boolean {
        val time = if (story.draft && story.scheduledPublishDateTimeAsDate != null) {
            story.scheduledPublishDateTimeAsDate.time
        } else if (story.published) {
            story.publishedDateTimeAsDate!!.time
        } else {
            return false
        }

        return DateUtils.toDate(startDate).time <= time &&
            DateUtils.toDate(endDate).time >= time
    }
}
