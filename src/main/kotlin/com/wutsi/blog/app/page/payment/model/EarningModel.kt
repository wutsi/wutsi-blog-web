package com.wutsi.blog.app.page.payment.model

data class EarningModel(
        val id: Long = -1,
        val month: Int = 0,
        val year: Int = 0,
        val amount: Long = 0,
        val currency: String = "",
        val amountText: String = ""
)
