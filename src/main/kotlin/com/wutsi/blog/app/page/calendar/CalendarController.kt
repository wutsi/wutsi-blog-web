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
import com.wutsi.blog.client.story.StoryStatus.draft
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
        date ?: return beginingOfTheWeek()

        try {
            return beginingOfTheWeek(LocalDate.parse(date))
        } catch (ex: Exception) {
            return beginingOfTheWeek()
        }
    }

    private fun loadStories(posts: List<CalendarPostModel>, startDate: LocalDate, endDate: LocalDate): List<CalendarStoryModel> {
        val userId = requestContext.currentUser()?.id ?: return emptyList()

        var all = mutableListOf<CalendarStoryModel>()
        all.addAll(posts.map { it.story })

        val storiesToPublish = storyService.search(SearchStoryRequest(
            status = draft,
            userIds = listOf(userId),
            limit = 1000
        )).filter { willPublish(it, startDate, endDate) }
        all.addAll(storiesToPublish.map { mapper.toCalendarStoryModel(it) })

        val result = all.distinctBy { it.id }
        val colors = arrayOf(
            "orange", "blue", "darkgreen", "darkred", "magenta"
        )
        var i = 0
        result.forEach { it.color = colors[i++ % colors.size] }
        return result
    }

    private fun beginingOfTheWeek(date: LocalDate = LocalDate.now()): LocalDate {
        var cur = date
        while (cur.dayOfWeek != SUNDAY)
            cur = date.plusDays(-1)
        return cur
    }

    private fun willPublish(story: StoryModel, startDate: LocalDate, endDate: LocalDate): Boolean {
        story.scheduledPublishDateTimeAsDate ?: return false

        val time = story.scheduledPublishDateTimeAsDate.time
        return DateUtils.toDate(startDate).time <= time &&
            DateUtils.toDate(endDate).time >= time
    }
}
