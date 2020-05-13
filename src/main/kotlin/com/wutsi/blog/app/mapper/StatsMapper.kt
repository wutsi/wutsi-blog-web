package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.StoryStatsModel
import com.wutsi.blog.client.stats.StatsDto
import org.springframework.stereotype.Service

@Service
class StatsMapper {
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
