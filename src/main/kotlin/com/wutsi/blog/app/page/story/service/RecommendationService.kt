package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.backend.RecommendationBackend
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.story.RecommendStoryRequest
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class RecommendationService(
        private val requestContext: RequestContext,
        private val backend: RecommendationBackend,
        private val stories: StoryService,
        private val logger: KVLogger
) {
    companion object {
        const val TOTAL_RECOMMENDATIONS = 9
    }

    fun search(storyId: Long): List<StoryModel> {
        val response = backend.search(RecommendStoryRequest(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = requestContext.deviceId(),
                limit = TOTAL_RECOMMENDATIONS
        ))

        logger.add("StoryIds", response.storyIds)
        logger.add("Count", response.storyIds.size)
        return if (response.storyIds.isEmpty()) emptyList() else stories.search(SearchStoryRequest(
                storyIds = response.storyIds,
                sortBy = StorySortStrategy.no_sort
        ))
    }
}

