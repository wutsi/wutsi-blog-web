package com.wutsi.blog.app.page.stats.service

import com.wutsi.blog.app.backend.StatsBackend
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.service.LocalizationService
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.stats.model.StatsStorySummaryModel
import com.wutsi.blog.app.page.stats.model.StatsUserSummaryModel
import com.wutsi.blog.app.page.stats.model.TrafficModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.client.stats.MonthlyTrafficStoryDto
import com.wutsi.blog.client.stats.SearchDailyStatsRequest
import com.wutsi.blog.client.stats.SearchMonthlyStatsStoryRequest
import com.wutsi.blog.client.stats.SearchMonthlyStatsUserRequest
import com.wutsi.blog.client.stats.SearchMonthlyTrafficStoryRequest
import com.wutsi.blog.client.stats.StatsType
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StoryStatus
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar

@Service
class StatsService(
        private val mapper: StatsMapper,
        private val backend: StatsBackend,
        private val storyBackend: StoryBackend,
        private val requestContext: RequestContext,
        private val localization: LocalizationService
) {
    fun user(year: Int, month: Int) : StatsUserSummaryModel {
        val stats = backend.search(SearchMonthlyStatsUserRequest(
                userId = requestContext.currentUser()?.id,
                year = year,
                month = month
        )).stats

        val overallTRT = backend.search(SearchMonthlyStatsStoryRequest(
                storyIds = arrayListOf(-1),
                year = year,
                month = month,
                type = StatsType.read_time
        )).stats
        return mapper.toStatsUserSummaryModel(stats, if (overallTRT.isEmpty()) null else overallTRT[0])
    }

    fun stories(year: Int, month: Int) : List<StatsStorySummaryModel> {
        val stats = backend.search(SearchMonthlyStatsStoryRequest(
                userId = requestContext.currentUser()?.id,
                year = year,
                month = month
        )).stats
        if (stats.isEmpty()){
            return emptyList()
        }

        val storyIds = stats.map { it.storyId }.toSet()
        val stories = storyBackend.search(SearchStoryRequest(
                storyIds = storyIds.toList(),
                status = StoryStatus.published,
                live = true,
                limit = 50
        )).stories
        return stories
                .map { mapper.toStatsStorySummaryModel(it, stats) }
                .filter { it.totalReadTime > 0 }
    }

    fun story(story: StoryModel, year: Int, month: Int) : StatsStorySummaryModel {
        val stats = backend.search(SearchMonthlyStatsStoryRequest(
                userId = story.user.id,
                year = year,
                month = month,
                storyIds = arrayListOf(story.id)
        )).stats
        if (stats.isEmpty()){
            return StatsStorySummaryModel()
        }

        return mapper.toStatsStorySummaryModel(story, stats)
    }

    fun traffic(story: StoryModel, year: Int, month: Int): List<TrafficModel> {
        val traffics = backend.search(SearchMonthlyTrafficStoryRequest(
                year = year,
                month = month,
                storyIds = arrayListOf(story.id)
        )).traffics
        if (traffics.isEmpty()){
            return emptyList()
        }

        val totalValue = traffics.sumByDouble { it.value.toDouble() }
        return traffics
                .map { TrafficModel(
                    source = trafficSource(it.source),
                    value = it.value,
                    percent = BigDecimal(100* it.value.toDouble() / totalValue).setScale(2, RoundingMode.HALF_EVEN)
                ) }
                .sortedByDescending { it.value }
    }

    fun traffic(year: Int, month: Int): List<TrafficModel> {
        val traffics = backend.search(SearchMonthlyTrafficStoryRequest(
                year = year,
                month = month,
                userId = requestContext.currentUser()!!.id
        )).traffics
        if (traffics.isEmpty()){
            return emptyList()
        }

        val totalValue = traffics.sumByDouble { it.value.toDouble() }
        return traffics
                .groupBy { it.source }
                .map {
                    val source = it.key
                    val value = sumValues(it.value)
                    TrafficModel(
                        source = trafficSource(source),
                        value = value,
                        percent = BigDecimal(100* value.toDouble() / totalValue).setScale(2, RoundingMode.HALF_EVEN)
                    )
                }
                .sortedByDescending { it.value }
    }

    private fun trafficSource(source: String): String {
        try {
            return localization.getMessage("traffic.$source")
        } catch (ex: Exception) {
            return source
        }
    }

    private fun sumValues(traffics: List<MonthlyTrafficStoryDto>) = traffics.sumByDouble { it.value.toDouble() }.toLong()

    fun barChartData(story: StoryModel, type: StatsType, year: Int, month: Int): BarChartModel {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month-1)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val startDate = cal.time
        val endDate = DateUtils.addDays(DateUtils.addMonths(startDate, 1), -1)

        val stats = backend.search(SearchDailyStatsRequest(
                targetIds = arrayListOf(story.id),
                startDate = startDate,
                endDate = endDate,
                type = StatsType.viewers
        )).stats
        return mapper.toBarChartModel(startDate, endDate, stats)
    }
}

