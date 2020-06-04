package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.StatsBackend
import com.wutsi.blog.app.mapper.StatsMapper
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.StoryStatsModel
import com.wutsi.blog.app.model.toastui.BarChartModel
import com.wutsi.blog.client.stats.SearchStatsRequest
import com.wutsi.blog.client.stats.StatsType
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Service
import java.util.Date

@Service
class StatsService(
        private val mapper: StatsMapper,
        private val backend: StatsBackend
) {
    fun story(story:StoryModel): StoryStatsModel {
        val startDate = story.publishedDateTimeAsDate
        val endDate = DateUtils.addDays(Date(), 1)

        val viewers = backend.search(SearchStatsRequest(
            targetIds = arrayListOf(story.id),
            startDate = startDate,
            endDate = endDate,
            type = StatsType.viewers
        )).stats

        val readTime = backend.search(SearchStatsRequest(
            targetIds = arrayListOf(story.id),
            startDate = startDate,
            endDate = endDate,
            type = StatsType.read_time
        )).stats
        return mapper.toStoryStatsModel(viewers, readTime)
    }

    fun barChartData(story: StoryModel, type: StatsType): BarChartModel {
        val startDate = story.publishedDateTimeAsDate!!
        val endDate = Date()

        val stats = backend.search(SearchStatsRequest(
                targetIds = arrayListOf(story.id),
                startDate = startDate,
                endDate = endDate,
                type = StatsType.viewers
        )).stats
        return mapper.toBarChartModel(startDate, endDate, stats)
    }
}

