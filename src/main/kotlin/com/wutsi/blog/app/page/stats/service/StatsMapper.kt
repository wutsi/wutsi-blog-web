package com.wutsi.blog.app.page.stats.service

import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.model.tui.BarChartSerieModel
import com.wutsi.blog.app.common.service.LocalizationService
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.stats.model.StatsStorySummaryModel
import com.wutsi.blog.app.page.stats.model.StatsUserSummaryModel
import com.wutsi.blog.app.page.stats.model.TrafficModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.core.util.DurationUtils
import com.wutsi.core.util.NumberUtils
import com.wutsi.stats.dto.StoryKpi
import com.wutsi.stats.dto.StoryTraffic
import com.wutsi.stats.dto.UserKpi
import com.wutsi.stats.dto.UserTraffic
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class StatsMapper(
    private val requestContext: RequestContext,
    private val localization: LocalizationService
) {
    fun toStatsUserSummaryModel(viewerKpis: List<UserKpi>, readTimeKpis: List<UserKpi>): StatsUserSummaryModel {
        val totalViews = viewerKpis.sumByDouble { it.value.toDouble() }.toLong()
        val totalReadTime = readTimeKpis.sumByDouble { it.value.toDouble() }.toLong()
        val averageReadTime = if (totalViews == 0L) 0 else totalReadTime / totalViews

        return StatsUserSummaryModel(
            totalViews = totalViews,
            totalViewsText = NumberUtils.toHumanReadable(totalViews),
            totalReadTime = totalReadTime,
            totalReadTimeText = DurationUtils.secondsToHumanReadable(totalReadTime),
            averageReadTime = averageReadTime
        )
    }

    fun toStatsStorySummaryModel(story: StoryModel, viewerKpis: List<StoryKpi>, readTimeKpis: List<StoryKpi>): StatsStorySummaryModel {
        val totalViews = viewerKpis.sumByDouble { it.value.toDouble() }.toLong()
        val totalReadTime = readTimeKpis.sumByDouble { it.value.toDouble() }.toLong()
        val averageReadTime = if (totalViews == 0L) 0 else totalReadTime / totalViews

        return StatsStorySummaryModel(
            story = story,
            totalViews = totalViews,
            totalViewsText = NumberUtils.toHumanReadable(totalViews),
            totalReadTime = totalReadTime,
            totalReadTimeText = DurationUtils.secondsToHumanReadable(totalReadTime),
            averageReadTime = averageReadTime
        )
    }

    fun toTrafficModel(obj: StoryTraffic, totalValue: Double): TrafficModel {
        return toTrafficModel(obj.name, obj.value.toDouble(), totalValue)
    }

    fun toTrafficModel(obj: UserTraffic, totalValue: Double): TrafficModel {
        return toTrafficModel(obj.name, obj.value.toDouble(), totalValue)
    }

    fun toBarChartModel(
        startDate: LocalDate,
        endDate: LocalDate,
        stats: List<StoryKpi>
    ): BarChartModel {
        val dates = toDateList(startDate, endDate)
        return BarChartModel(
            categories = dates.map { it.toString() },
            series = arrayListOf(
                BarChartSerieModel(
                    name = requestContext.getMessage("page.stats.kpi.daily_views"),
                    data = toBarChartSerie(dates, stats)
                )
            )
        )
    }

    private fun toDateList(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        var cur = endDate
        val result = mutableListOf<LocalDate>()
        while (cur.isAfter(startDate) || cur == startDate) {
            result.add(0, cur)
            cur = cur.minusDays(1)
        }
        return result
    }

    private fun toBarChartSerie(dates: List<LocalDate>, stats: List<StoryKpi>): List<Double> {
        val statMap = stats.map { it.date to it }.toMap()
        return dates.map {
            val stat = statMap[it]
            if (stat == null) 0.0 else stat.value.toDouble()
        }
    }

    private fun toTrafficModel(source: String?, value: Double, totalValue: Double): TrafficModel {
        val percent = BigDecimal(100.0 * value / totalValue)
            .setScale(2, RoundingMode.HALF_EVEN)

        return TrafficModel(
            source = trafficSource(source),
            value = value.toLong(),
            percent = percent,
            percentAsInt = if (percent.toDouble() < 1.0) 1 else percent.toInt(),
            percentText = "$percent%"
        )
    }

    private fun trafficSource(source: String?): String {
        try {
            return if (source.isNullOrEmpty())
                localization.getMessage("traffic.direct")
            else
                localization.getMessage("traffic.$source")
        } catch (ex: Exception) {
            return source!!
        }
    }
}
