package com.wutsi.blog.app.page.monetization.model

import com.wutsi.blog.app.common.model.MoneyModel

data class PlanRateModel(
    val id: Long = -1,
    val yearly: MoneyModel = MoneyModel(),
    val monthly: MoneyModel = MoneyModel()
)
