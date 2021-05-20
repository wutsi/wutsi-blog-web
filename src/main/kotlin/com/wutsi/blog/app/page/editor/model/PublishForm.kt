package com.wutsi.blog.app.page.editor.model

import com.wutsi.blog.client.story.StoryAccess
import com.wutsi.blog.client.story.StoryAccess.PUBLIC

data class PublishForm(
    val id: Long = -1,
    val title: String = "",
    val tagline: String = "",
    val summary: String = "",
    val topicId: String = "",
    val tags: List<String> = emptyList(),
    val publishNow: Boolean = true,
    val scheduledPublishDate: String = "",
    val publishToSocialMedia: Boolean = false,
    val socialMediaMessage: String = "",
    val access: StoryAccess = PUBLIC
)
