package com.wutsi.blog.app.page.like.service

import com.wutsi.blog.app.backend.LikeBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.comment.model.CreateCommentForm
import com.wutsi.blog.app.page.like.model.LikeCountModel
import com.wutsi.blog.app.page.like.model.LikeModel
import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.like.CountLikeRequest
import com.wutsi.blog.client.like.CreateLikeRequest
import com.wutsi.blog.client.like.LikeDto
import com.wutsi.blog.client.like.SearchLikeRequest
import org.springframework.stereotype.Service

@Service
class LikeService(
        private val backend: LikeBackend,
        private val mapper: LikeMapper,
        private val requestContext: RequestContext
) {
    fun count(storyIds: List<Long>): List<LikeCountModel> {
        val counts = backend.count(CountLikeRequest(
                storyIds = storyIds
        )).counts

        return counts.map { mapper.toLikeCountModel(it) }
    }

    fun search(storyIds: List<Long>): List<LikeModel> {
        val likes = backend.search(SearchLikeRequest(
                storyIds = storyIds,
                userId = requestContext.currentUser()!!.id
        )).likes

        return likes.map { mapper.toLikeModel(it) }
    }

    fun create(storyId: Long): LikeModel {
        val likeResponse = backend.create(CreateLikeRequest(
                storyId = storyId,
                userId = requestContext.currentUser()!!.id
        ))

        return mapper.toLikeModel(LikeDto(
                id = likeResponse.likeId,
                storyId = storyId,
                userId = requestContext.currentUser()!!.id
        ))
    }

    fun delete(id: Long) {
        backend.delete(id)
    }
}