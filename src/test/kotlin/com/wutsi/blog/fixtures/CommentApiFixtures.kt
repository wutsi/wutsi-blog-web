package com.wutsi.blog.fixtures

import com.wutsi.blog.client.comment.CommentCountDto
import com.wutsi.blog.client.comment.CommentDto
import java.util.Date

object CommentApiFixtures {
    fun createCommentDto(userId: Long, storyId: Long) = CommentDto(
        id = System.currentTimeMillis(),
        userId = userId,
        storyId = storyId,
        creationDateTime = Date(),
        modificationDateTime = Date()
    )

    fun createCommentCountDto(storyId: Long, value: Long) = CommentCountDto(
        storyId = storyId,
        value = value
    )
}
