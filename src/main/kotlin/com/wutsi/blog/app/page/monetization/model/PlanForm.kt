package com.wutsi.blog.app.page.monetization.model

data class PlanForm(
    val id: Long? = null,
    val name: String = "",
    val description: String = "",
    val yearly: String = "",
    val currency: String = ""
)
