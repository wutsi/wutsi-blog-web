package com.wutsi.blog.app.page.comment.service

import com.wutsi.blog.app.backend.CommentBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.comment.model.CommentCountModel
import com.wutsi.blog.app.page.comment.model.CommentModel
import com.wutsi.blog.app.page.comment.model.CreateCommentForm
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.comment.SearchCommentRequest
import com.wutsi.blog.client.user.SearchUserRequest
import org.springframework.stereotype.Service

@Service
class CommentService(
        private val backend: CommentBackend,
        private val mapper: CommentMapper,
        private val users: UserService,
        private val requestContext: RequestContext
) {
    fun count(storyId: Long): CommentCountModel {
        val count = backend.count(storyId).count
        return mapper.toCommentCountModel(count)
    }

    fun list(storyId: Long, limit: Int=50, offset: Int = 0): List<CommentModel> {
        val comments = backend.search(SearchCommentRequest(
                storyId = storyId,
                limit = limit,
                offset = offset
        )).comments

        val userIds = comments.map { it.userId }.toSet()
        val users = users.search(SearchUserRequest(
                userIds = userIds.toList(),
                limit = userIds.size,
                offset = 0
        )).map { it.id to it }.toMap()

        return comments.map { mapper.toCommentModel(it, users) }
    }

    fun create(form: CreateCommentForm): Long {
        return backend.create(CreateCommentRequest(
                storyId = form.storyId,
                text = form.text,
                userId = requestContext.currentUser()!!.id
        )).commentId
    }
}
