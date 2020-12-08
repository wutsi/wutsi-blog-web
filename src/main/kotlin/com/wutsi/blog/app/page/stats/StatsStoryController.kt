package com.wutsi.blog.app.page.stats

import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.stats.service.StatsService
import com.wutsi.blog.app.page.story.AbstractStoryController
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.stats.StatsType
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Controller
@RequestMapping()
class StatsStoryController(
    private val stats: StatsService,
    service: StoryService,
    requestContext: RequestContext
) : AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.STATS_STORY

    override fun requiredPermissions() = listOf(Permission.owner)

    @GetMapping("/stats/story/{id}")
    fun index(
        @PathVariable id: Long,
        @RequestParam(required = false) year: Int? = null,
        @RequestParam(required = false) month: Int? = null,
        model: Model
    ): String {
        val cal = Calendar.getInstance()
        val currentYear = if (year == null) cal.get(Calendar.YEAR) else year
        val currentMonth = if (month == null) cal.get(Calendar.MONTH) + 1 else month

        val story = getStory(id)
        model.addAttribute("story", story)

        loadPagination(story, currentYear, currentMonth, model)
        loadSummary(story, currentYear, currentMonth, model)
        loadTraffic(story, currentYear, currentMonth, model)
        return "page/stats/story"
    }

    @ResponseBody
    @GetMapping(value = ["/stats/story/{id}/bar-chart-data"], produces = ["application/json"])
    fun barChartData(
        @PathVariable id: Long,
        @RequestParam type: StatsType,
        @RequestParam(required = false) year: Int,
        @RequestParam(required = false) month: Int
    ): BarChartModel {
        val story = getStory(id)
        return stats.barChartData(story, type, year, month)
    }

    private fun loadSummary(story: StoryModel, currentYear: Int, currentMonth: Int, model: Model) {
        val summary = stats.story(story, currentYear, currentMonth)
        model.addAttribute("summary", summary)
    }

    private fun loadTraffic(story: StoryModel, currentYear: Int, currentMonth: Int, model: Model) {
        val traffics = stats.traffic(story, currentYear, currentMonth)
        model.addAttribute("traffics", traffics)
    }

    private fun loadPagination(story: StoryModel, currentYear: Int, currentMonth: Int, model: Model) {
        val fmt = SimpleDateFormat("MMM yyyy")

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, currentYear)
        cal.set(Calendar.MONTH, currentMonth - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val today = cal.time
        model.addAttribute("currentMonth", fmt.format(today))

        val nextMonth = DateUtils.addMonths(today, 1)
        model.addAttribute("nextMonth", fmt.format(nextMonth))
        model.addAttribute("nextMonthUrl", url(story, nextMonth))

        val previousMonth = DateUtils.addMonths(today, -1)
        model.addAttribute("previousMonth", fmt.format(previousMonth))
        model.addAttribute("previousMonthUrl", url(story, previousMonth))

        model.addAttribute("year", currentYear)
        model.addAttribute("month", currentMonth)
    }

    private fun url(story: StoryModel, date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return "/stats/story/${story.id}?year=$year&month=$month"
    }
}
