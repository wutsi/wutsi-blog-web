package com.wutsi.blog.app.page.monetization.model

data class PlanModel(
    val id: Long = -1,
    val partnerId: Long = -1,
    val name: String = "",
    val description: String = "",
    val active: Boolean = true,
    val rate: PlanRateModel = PlanRateModel(),
    val internationalRate: PlanRateModel = PlanRateModel()
)
