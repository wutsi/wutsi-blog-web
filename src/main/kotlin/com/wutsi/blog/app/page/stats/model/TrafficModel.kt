package com.wutsi.blog.app.page.stats.model

import com.wutsi.blog.app.common.model.MoneyModel
import com.wutsi.blog.app.page.story.model.StoryModel
import java.math.BigDecimal

data class TrafficModel (
        val source: String = "",
        val value: Long = 0,
        val percent: BigDecimal = BigDecimal.ZERO,
        val percentAsInt: Int,
        val percentText: String
)
