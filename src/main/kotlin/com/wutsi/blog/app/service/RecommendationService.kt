package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.RecommendationBackend
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.client.recommendation.RecommendStoryRequest
import com.wutsi.blog.client.story.SearchStoryRequest
import org.springframework.stereotype.Service

@Service
class RecommendationService(
        private val requestContext: RequestContext,
        private val backend: RecommendationBackend,
        private val stories: StoryService
) {
    fun search(storyId: Long): List<StoryModel> {
        val response = backend.search(RecommendStoryRequest(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                language = requestContext.storyLocale()?.language,
                limit = 3
        ))
        if (response.storyIds.isEmpty()) {
            return emptyList()
        }

        return stories.search(SearchStoryRequest(
                storyIds = response.storyIds
        ))
    }
}

