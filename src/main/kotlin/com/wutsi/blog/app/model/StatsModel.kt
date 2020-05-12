package com.wutsi.blog.app.model

data class StatsModel(
        val date: String,
        val type: String,
        val value: Long,
        val targetId: Long
)
