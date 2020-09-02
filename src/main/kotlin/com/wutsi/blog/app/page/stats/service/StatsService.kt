package com.wutsi.blog.app.page.stats.service

import com.wutsi.blog.app.backend.StatsBackend
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.stats.model.StatsStorySummaryModel
import com.wutsi.blog.app.page.stats.model.StatsUserSummaryModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.client.stats.SearchStatsRequest
import com.wutsi.blog.client.stats.SearchStatsStoryRequest
import com.wutsi.blog.client.stats.SearchStatsUserRequest
import com.wutsi.blog.client.stats.StatsType
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StoryStatus
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Service
import java.util.Calendar

@Service
class StatsService(
        private val mapper: StatsMapper,
        private val backend: StatsBackend,
        private val storyBackend: StoryBackend,
        private val requestContext: RequestContext
) {
    fun user(year: Int, month: Int) : StatsUserSummaryModel {
        val stats = backend.search(SearchStatsUserRequest(
                userId = requestContext.currentUser()?.id,
                year = year,
                month = month
        )).stats

        val overallTRT = backend.search(SearchStatsStoryRequest(
                storyIds = arrayListOf(-1),
                year = year,
                month = month,
                type = StatsType.read_time
        )).stats
        return mapper.toStatsUserSummaryModel(stats, if (overallTRT.isEmpty()) null else overallTRT[0])
    }

    fun stories(year: Int, month: Int) : List<StatsStorySummaryModel> {
        val stats = backend.search(SearchStatsStoryRequest(
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
        val stats = backend.search(SearchStatsStoryRequest(
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


    fun barChartData(story: StoryModel, type: StatsType, year: Int, month: Int): BarChartModel {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month-1)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val startDate = cal.time
        val endDate = DateUtils.addDays(DateUtils.addMonths(startDate, 1), -1)

        val stats = backend.search(SearchStatsRequest(
                targetIds = arrayListOf(story.id),
                startDate = startDate,
                endDate = endDate,
                type = StatsType.viewers
        )).stats
        return mapper.toBarChartModel(startDate, endDate, stats)
    }
}

