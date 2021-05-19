package com.wutsi.blog.app.page.monetization.model

data class SubscriptionModel(
    val id: Long = -1,
    val plan: PlanModel = PlanModel(),
    val subscriberId: Long = -1
)
