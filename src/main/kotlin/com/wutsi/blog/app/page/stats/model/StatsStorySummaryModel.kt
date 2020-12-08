package com.wutsi.blog.app.page.stats.model

import com.wutsi.blog.app.common.model.MoneyModel
import com.wutsi.blog.app.page.story.model.StoryModel

data class StatsStorySummaryModel(
    val story: StoryModel = StoryModel(),
    val totalViews: Long = 0,
    val totalViewsText: String = "",
    val totalReadTime: Long = 0,
    val totalReadTimeText: String = "",
    val averageReadTime: Long = 0,
    val averageReadTimeText: String = "",
    val earnings: MoneyModel? = null
)
