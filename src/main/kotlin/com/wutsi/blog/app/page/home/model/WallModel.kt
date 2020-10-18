package com.wutsi.blog.app.page.home.model

import com.wutsi.blog.app.page.story.model.StoryModel

data class WallModel(
        val mainStory: StoryModel? = null,
        val stories: List<StoryModel> = emptyList(),
        val featureStories: List<StoryModel> = emptyList(),
        val popularStories: List<StoryModel> = emptyList()
)
