package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.StoryStatsModel
import com.wutsi.blog.client.stats.StatsDto
import org.springframework.stereotype.Service

@Service
class StatsMapper {
    fun toStoryStatsModel(viewers: List<StatsDto>, readTime: List<StatsDto>): StoryStatsModel {
        val viewers = viewers.size
        val readTime = readTime.sumByDouble { it.value.toDouble() }.toLong()
        val averageReadingTime = if (viewers == 0) 0 else readTime/viewers
        return StoryStatsModel(

                totalViews = viewers.toLong(),
                averageReadTime = averageReadingTime,
                averageReadTimeText = displayTime(averageReadingTime)
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
