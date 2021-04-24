package com.wutsi.blog.app.page.stats.service

import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.stats.model.StatsStorySummaryModel
import com.wutsi.blog.app.page.stats.model.StatsUserSummaryModel
import com.wutsi.blog.app.page.stats.model.TrafficModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.client.stats.StatsType
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.stats.StatsApi
import com.wutsi.stats.dto.KpiType
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class StatsService(
    private val statsApi: StatsApi,
    private val mapper: StatsMapper,
    private val storyService: StoryService,

    private val requestContext: RequestContext
) {
    companion object {
        const val LIMIT = 1000
    }

    fun user(year: Int, month: Int): StatsUserSummaryModel {
        val userId = requestContext.currentUser()!!.id
        val viewerKpis = statsApi.userMonthlyKpis(
            userId = userId,
            year = year,
            month = month,
            type = KpiType.VIEWER.name,
            limit = LIMIT
        ).kpis

        val readTimeKpis = statsApi.userMonthlyKpis(
            userId = userId,
            year = year,
            month = month,
            type = KpiType.READ_TIME.name,
            limit = LIMIT
        ).kpis
        return mapper.toStatsUserSummaryModel(viewerKpis, readTimeKpis)
    }

    fun stories(year: Int, month: Int): List<StatsStorySummaryModel> {
        val kpis = statsApi.storyMonthlyKpis(
            userId = requestContext.currentUser()?.id,
            year = year,
            month = month,
            storyId = null,
            type = null,
            offset = 0,
            limit = LIMIT
        ).kpis

        val storyIds = kpis.map { it.storyId }.toSet()
        if (storyIds.isEmpty())
            return emptyList()

        val request = SearchStoryRequest(
            storyIds = storyIds.toList(),
            status = StoryStatus.published,
            live = true,
            limit = storyIds.size,
            offset = 0
        )
        val stories = storyService.search(request)

        return stories
            .map { mapper.toStatsStorySummaryModel(it, kpis) }
            .filter { it.totalReadTime > 0 }
    }

    fun story(story: StoryModel, year: Int, month: Int): StatsStorySummaryModel {
        val viewerKpis = statsApi.storyMonthlyKpis(
            storyId = story.id,
            year = year,
            month = month,
            type = KpiType.VIEWER.name,
            limit = LIMIT,
            offset = 0
        ).kpis

        val readTimeKpis = statsApi.storyMonthlyKpis(
            storyId = story.id,
            year = year,
            month = month,
            type = KpiType.READ_TIME.name,
            limit = LIMIT,
            offset = 0
        ).kpis

        return mapper.toStatsStorySummaryModel(story, viewerKpis, readTimeKpis)
    }

    fun traffic(story: StoryModel, year: Int, month: Int): List<TrafficModel> {
        val traffics = statsApi.storyMonthlyTraffic(
            storyId = story.id,
            month = month,
            year = year,
            limit = LIMIT
        ).traffics
        val totalValue = traffics.sumByDouble { it.value.toDouble() }

        return traffics
            .map { mapper.toTrafficModel(it, totalValue) }
            .sortedByDescending { it.value }
    }

    fun traffic(year: Int, month: Int): List<TrafficModel> {
        val traffics = statsApi.userMonthlyTraffic(
            userId = requestContext.currentUser()!!.id,
            month = month,
            year = year,
            limit = LIMIT
        ).traffics
        val totalValue = traffics.sumByDouble { it.value.toDouble() }

        return traffics
            .map { mapper.toTrafficModel(it, totalValue) }
            .sortedByDescending { it.value }
    }

    fun barChartData(story: StoryModel, type: StatsType, year: Int, month: Int): BarChartModel {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.plusMonths(1).minusDays(1)
        val viewerKpis = statsApi.storyMonthlyKpis(
            storyId = story.id,
            type = KpiType.VIEWER.name,
            year = year,
            month = month,
            limit = LIMIT
        ).kpis

        return mapper.toBarChartModel(startDate, endDate, viewerKpis)
    }
}
