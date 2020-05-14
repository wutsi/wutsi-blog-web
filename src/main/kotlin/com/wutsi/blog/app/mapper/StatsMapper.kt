package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.StoryStatsModel
import com.wutsi.blog.app.model.toastui.BarChartModel
import com.wutsi.blog.app.model.toastui.BarChartSerieModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.client.stats.StatsDto
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date

@Service
class StatsMapper(
        private val requestContext: RequestContext
) {
    fun toStoryStatsModel(viewers: List<StatsDto>, readTime: List<StatsDto>): StoryStatsModel {
        val totalViews = viewers.sumByDouble { it.value.toDouble() }.toLong()
        val totalReadTime = readTime.sumByDouble { it.value.toDouble() }.toLong()
        val averageReadTime = if (totalViews == 0L) 0 else totalReadTime/totalViews
        return StoryStatsModel(

                totalViews = totalViews,
                averageReadTime = averageReadTime,
                averageReadTimeText = displayTime(averageReadTime)
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

    private fun displayTime(value: Long): String {
        if (value == 0L) {
            return ""
        }
        if (value < 60) {
            return "${value}s"
        } else {
            val minute = value / 60
            val seconds = value % 60

            return if (seconds == 0L) "${minute}m" else "${minute}m${seconds}s"
        }
    }
}
