package com.wutsi.blog.app.page.payment.service

import com.wutsi.blog.app.backend.EarningBackend
import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.payment.SearchEarningRequest
import org.springframework.stereotype.Service

@Service
class EarningService(
        private val mapper: PaymentMapper,
        private val backend: EarningBackend,
        private val requestContext: RequestContext
) {
    fun total(year: Int): String {
        val earnings = backend.search(SearchEarningRequest(
                userId = requestContext.currentUser()?.id,
                year = year
        )).earnings
        if (earnings.isEmpty()) {
            return ""
        }

        val amount = earnings.sumByDouble { it.amount.toDouble() }.toLong()
        val currency = earnings[0].currency
        return mapper.formatMoney(amount, currency)
    }
    fun barChartData(year: Int): BarChartModel {
        val earnings = backend.search(SearchEarningRequest(
                userId = requestContext.currentUser()?.id,
                year = year
        )).earnings
        return mapper.toBarChartModel(year, earnings)
    }

}

