package com.wutsi.blog.app.page.story.service

import com.wutsi.similarity.SimilarityApi
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val similarityApi: SimilarityApi
) {
    fun similar(storyId: Long, limit: Int): List<Long> =
        similarityApi.getSimilarStories(storyId, limit).stories.map { it.storyId }
}
