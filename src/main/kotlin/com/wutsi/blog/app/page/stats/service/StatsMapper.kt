package com.wutsi.blog.app.page.stats.service

import com.wutsi.blog.app.page.stats.model.StatsStorySummaryModel
import com.wutsi.blog.app.page.stats.model.StatsUserSummaryModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.stats.model.BarChartModel
import com.wutsi.blog.app.page.stats.model.BarChartSerieModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.client.stats.StatsDto
import com.wutsi.blog.client.stats.StatsStoryDto
import com.wutsi.blog.client.stats.StatsType
import com.wutsi.blog.client.stats.StatsUserDto
import com.wutsi.blog.client.story.StorySummaryDto
import com.wutsi.core.util.DurationUtils
import com.wutsi.core.util.NumberUtils
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Service
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date

@Service
class StatsMapper(
        private val requestContext: RequestContext
) {
    fun toStatsUserSummaryModel(stats: List<StatsUserDto>, overallTRL: StatsStoryDto?): StatsUserSummaryModel {
        val totalViews = stats
                .filter { it.type == StatsType.viewers }
                .sumByDouble { it.value.toDouble() }.toLong()

        val totalReadTime = stats
                .filter { it.type == StatsType.read_time }
                .sumByDouble { it.value.toDouble() }.toLong()

        val averageReadTime = if (totalViews == 0L) 0 else totalReadTime/totalViews

        val percentReadTime: Double = if (overallTRL != null && overallTRL.value != 0L) {
            totalReadTime.toDouble() / overallTRL.value.toDouble()
        } else {
            0.0
        }
        val fmt = NumberFormat.getPercentInstance()

        return StatsUserSummaryModel(
                totalViews = totalViews,
                totalViewsText = NumberUtils.toHumanReadable(totalViews),
                totalReadTime = totalReadTime,
                totalReadTimeText = DurationUtils.secondsToHumanReadable(totalReadTime),
                averageReadTime = averageReadTime,
                averageReadTimeText = DurationUtils.secondsToHumanReadable(averageReadTime),
                percentReadTimeText = fmt.format(percentReadTime)
        )
    }

    fun toStatsStorySummaryModel(story: StorySummaryDto, stats: List<StatsStoryDto>): StatsStorySummaryModel {
        return toStatsStorySummaryModel(story.id, story.title, stats)
    }

    fun toStatsStorySummaryModel(story: StoryModel, stats: List<StatsStoryDto>): StatsStorySummaryModel {
        return toStatsStorySummaryModel(story.id, story.title, stats)
    }

    fun toStatsStorySummaryModel(id: Long, title: String?, stats: List<StatsStoryDto>): StatsStorySummaryModel {
        val totalViews = stats
                .filter { it.type == StatsType.viewers && it.storyId == id }
                .sumByDouble { it.value.toDouble() }.toLong()

        val totalReadTime = stats
                .filter { it.type == StatsType.read_time&& it.storyId == id  }
                .sumByDouble { it.value.toDouble() }.toLong()

        val averageReadTime = if (totalViews == 0L) 0 else totalReadTime/totalViews

        return StatsStorySummaryModel(
                storyId = id,
                title = title,
                totalViews = totalViews,
                totalViewsText = NumberUtils.toHumanReadable(totalViews),
                totalReadTime = totalReadTime,
                totalReadTimeText = DurationUtils.secondsToHumanReadable(totalReadTime),
                averageReadTime = averageReadTime,
                averageReadTimeText = DurationUtils.secondsToHumanReadable(averageReadTime)
        )
    }

    fun toStoryStatsModel(viewers: List<StatsDto>, readTime: List<StatsDto>): StatsStorySummaryModel {
        val totalViews = viewers.sumByDouble { it.value.toDouble() }.toLong()
        val totalReadTime = readTime.sumByDouble { it.value.toDouble() }.toLong()
        val averageReadTime = if (totalViews == 0L) 0 else totalReadTime/totalViews
        return StatsStorySummaryModel(
                totalViews = totalViews,
                totalViewsText = NumberUtils.toHumanReadable(totalViews),
                averageReadTime = averageReadTime,
                averageReadTimeText = DurationUtils.secondsToHumanReadable(averageReadTime),
                totalReadTime = totalReadTime,
                totalReadTimeText = DurationUtils.secondsToHumanReadable(totalReadTime)
        )
    }

    fun toBarChartModel(
            startDate: Date,
            endDate: Date,
            stats: List<StatsDto>
    ): BarChartModel {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val dates = toDateList(startDate, endDate, fmt, 31)
        return BarChartModel(
                categories = dates.map { fmt.format(it) },
                series = arrayListOf(
                        BarChartSerieModel(
                                name = requestContext.getMessage("page.stats.kpi.daily_views"),
                                data = toSerieData(dates, stats)
                        )
                )
        )
    }

    private fun toSerieData(dates: List<Date>, stats: List<StatsDto>): List<Double> {
        val statMap = stats.map { it.date to it }.toMap()
        return dates.map{
            val stat = statMap[it]
            if (stat == null) 0.0 else stat.value.toDouble()
        }
    }

    private fun toDateList(startDate: Date, endDate: Date, fmt: SimpleDateFormat, maxDates: Int): List<Date> {
        val start = fmt.parse(fmt.format(startDate))
        val end = fmt.parse(fmt.format(endDate))

        var cur = end
        val result = mutableListOf<Date>()
        while (cur.after(start) || cur == start){
            result.add(0, cur)
            cur = DateUtils.addDays(cur, -1)
            if (result.size >= maxDates){
                break
            }
        }
        return result
    }
}
