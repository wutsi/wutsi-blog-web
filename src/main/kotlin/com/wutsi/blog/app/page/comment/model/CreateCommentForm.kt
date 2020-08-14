package com.wutsi.blog.app.page.comment.model

data class CreateCommentForm(
        val storyId: Long = -1,
        val text: String = ""
)
