package com.wutsi.blog.app.page.stats

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.stats.service.StatsService
import com.wutsi.blog.app.util.PageName
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Controller
@RequestMapping()
class StatsUserController(
        private val stats: StatsService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.STATS_USER

    @GetMapping("/stats")
    fun index(
            @RequestParam(required = false) year: Int? = null,
            @RequestParam(required = false) month: Int? = null,
            model: Model
    ): String {
        val cal = Calendar.getInstance()
        val currentYear = if (year == null) cal.get(Calendar.YEAR) else year
        val currentMonth = if (month == null) cal.get(Calendar.MONTH)+1 else month

        loadUserSummary(currentYear, currentMonth, model)
        loadPagination(currentYear, currentMonth, model)
        loadStoriesSummary(currentYear, currentMonth, model)
        return "page/stats/user"
    }

    private fun loadUserSummary(currentYear: Int, currentMonth: Int, model: Model) {
        val summary = stats.user(
                year = currentYear,
                month = currentMonth
        )
        model.addAttribute("summary", summary)
    }

    private fun loadPagination(currentYear: Int, currentMonth: Int, model: Model) {
        val fmt = SimpleDateFormat("MMM yyyy")

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, currentYear)
        cal.set(Calendar.MONTH, currentMonth-1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val today = cal.time
        model.addAttribute("currentMonth", fmt.format(today))

        val nextMonth = DateUtils.addMonths(today, 1)
        model.addAttribute("nextMonth", fmt.format(nextMonth))
        model.addAttribute("nextMonthUrl", url(nextMonth))

        val previousMonth = DateUtils.addMonths(today, -1)
        model.addAttribute("previousMonth", fmt.format(previousMonth))
        model.addAttribute("previousMonthUrl", url(previousMonth))

        model.addAttribute("year", currentYear)
        model.addAttribute("month", currentMonth)
    }

    private fun loadStoriesSummary(currentYear: Int, currentMonth: Int, model: Model) {
        val storiesSummary = stats.stories(currentYear, currentMonth)
                .sortedBy { it.title }
        model.addAttribute("storySummaries", storiesSummary)
    }

    private fun url(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)+1
        return "/stats?year=$year&month=$month"
    }
}
