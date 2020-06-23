package com.wutsi.blog.app.page.stats.model

data class BarChartModel (
        val categories: List<String>,
        val series: List<BarChartSerieModel>
)
