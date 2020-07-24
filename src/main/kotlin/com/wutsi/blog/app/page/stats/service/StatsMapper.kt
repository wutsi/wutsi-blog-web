package com.wutsi.blog.app.page.stats.service

import com.wutsi.blog.app.common.model.MoneyModel
import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.model.tui.BarChartSerieModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.stats.model.StatsStorySummaryModel
import com.wutsi.blog.app.page.stats.model.StatsUserSummaryModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryMapper
import com.wutsi.blog.client.stats.StatsDto
import com.wutsi.blog.client.stats.StatsStoryDto
import com.wutsi.blog.client.stats.StatsType
import com.wutsi.blog.client.stats.StatsUserDto
import com.wutsi.blog.client.story.StorySummaryDto
import com.wutsi.blog.client.story.WPPStatus
import com.wutsi.core.util.DurationUtils
import com.wutsi.core.util.NumberUtils
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Service
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date

@Service
class StatsMapper(
        private val storyMapper: StoryMapper,
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
        val model = storyMapper.toStoryModel(story)
        return toStatsStorySummaryModel(model, stats)
    }

    fun toStatsStorySummaryModel(story: StoryModel, stats: List<StatsStoryDto>): StatsStorySummaryModel {
        val id = story.id
        val totalViews = stats
                .filter { it.type == StatsType.viewers && it.storyId == id }
                .sumByDouble { it.value.toDouble() }.toLong()

        val totalReadTime = stats
                .filter { it.type == StatsType.read_time && it.storyId == id  }
                .sumByDouble { it.value.toDouble() }.toLong()

        val averageReadTime = if (totalViews == 0L) 0 else totalReadTime/totalViews

        val totalEarnings = stats
                .filter { it.type == StatsType.wpp_earning && it.storyId == id  }
                .sumByDouble { it.value.toDouble() }.toLong()

        return StatsStorySummaryModel(
                story = story,
                totalViews = totalViews,
                totalViewsText = NumberUtils.toHumanReadable(totalViews),
                totalReadTime = totalReadTime,
                totalReadTimeText = DurationUtils.secondsToHumanReadable(totalReadTime),
                averageReadTime = averageReadTime,
                averageReadTimeText = DurationUtils.secondsToHumanReadable(averageReadTime),
                earnings = if (story.wppStatus == WPPStatus.approved) MoneyModel(totalEarnings, "XAF") else null
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
