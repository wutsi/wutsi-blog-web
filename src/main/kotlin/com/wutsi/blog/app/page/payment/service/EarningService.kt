package com.wutsi.blog.app.page.payment.service

import com.wutsi.blog.app.common.model.MoneyModel
import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.earning.EarningApi
import org.springframework.stereotype.Service

@Service
class EarningService(
    private val mapper: PaymentMapper,
    private val api: EarningApi,
    private val requestContext: RequestContext
) {
    fun total(year: Int): MoneyModel {
        val user = requestContext.currentUser() ?: return MoneyModel()

        val earnings = api.userEarnings(
            userId = user.id,
            year = year
        ).earnings
        if (earnings.isEmpty()) {
            return MoneyModel()
        }

        val amount = earnings.sumByDouble { it.amount.toDouble() }.toLong()
        return MoneyModel(amount, earnings[0].currency)
    }

    fun barChartData(year: Int): BarChartModel {
        val user = requestContext.currentUser()
        val earnings = api.userEarnings(
            userId = user!!.id,
            year = year
        ).earnings
        return mapper.toBarChartModel(year, earnings)
    }
}
