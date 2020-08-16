package com.wutsi.blog.app.page.comment.model

data class CommentCountModel(
        val storyId: Long = -1,
        val value: Long = 0,
        val valueText: String = "",
        val text: String = ""
)
