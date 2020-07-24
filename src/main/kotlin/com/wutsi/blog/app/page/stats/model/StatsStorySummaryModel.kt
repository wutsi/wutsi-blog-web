package com.wutsi.blog.app.page.stats.model

import com.wutsi.blog.app.common.model.MoneyModel

data class StatsStorySummaryModel (
        val storyId: Long = -1,
        val title: String? = "",
        val totalViews: Long = 0,
        val totalViewsText: String = "",
        val totalReadTime: Long = 0,
        val totalReadTimeText: String = "",
        val averageReadTime: Long = 0,
        val averageReadTimeText: String = "",
        val earnings: MoneyModel? = null
)
