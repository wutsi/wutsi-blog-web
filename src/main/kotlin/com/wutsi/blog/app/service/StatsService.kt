package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.StatsBackend
import com.wutsi.blog.app.mapper.StatsMapper
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.StoryStatsModel
import com.wutsi.blog.client.stats.SearchStatsRequest
import com.wutsi.blog.client.stats.StatsType
import org.springframework.stereotype.Service
import java.util.Date

@Service
class StatsService(
        private val mapper: StatsMapper,
        private val backend: StatsBackend
) {
    fun story(story:StoryModel): StoryStatsModel {
        val viewers = backend.search(SearchStatsRequest(
                targetId = story.id,
                endDate = Date(),
                type = StatsType.viewers
        )).stats

        val readTime = backend.search(SearchStatsRequest(
            targetId = story.id,
            endDate = Date(),
            type = StatsType.read_time
        )).stats
        return mapper.toStoryStatsModel(viewers, readTime)
    }
}

