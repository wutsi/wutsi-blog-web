package com.wutsi.blog.app.page.stats.model

data class StatsUserSummaryModel (
        val totalViews: Long,
        val totalViewsText: String,
        val totalReadTime: Long,
        val totalReadTimeText: String,
        val averageReadTime: Long,
        val averageReadTimeText: String,
        val percentReadTimeText: String
)
