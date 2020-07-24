package com.wutsi.blog.app.page.payment.model

import com.wutsi.blog.app.common.model.MoneyModel

data class EarningModel(
        val id: Long = -1,
        val month: Int = 0,
        val year: Int = 0,
        val amount: MoneyModel = MoneyModel()
)
