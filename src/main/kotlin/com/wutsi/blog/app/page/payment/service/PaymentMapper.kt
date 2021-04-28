package com.wutsi.blog.app.page.payment.service

import com.wutsi.blog.app.common.model.MoneyModel
import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.model.tui.BarChartSerieModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.payment.model.EarningModel
import com.wutsi.earning.dto.Earning
import org.springframework.stereotype.Service

@Service
class PaymentMapper(private val requestContext: RequestContext) {
    fun toEarningModel(obj: Earning) = EarningModel(
        id = obj.id,
        year = obj.date.year,
        month = obj.date.monthValue,
        amount = MoneyModel(obj.amount, obj.currency)
    )

    fun toBarChartModel(
        year: Int,
        earnings: List<Earning>
    ): BarChartModel {
        val months = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        return BarChartModel(
            categories = toDateList(year, months),
            series = arrayListOf(
                BarChartSerieModel(
                    name = requestContext.getMessage("page.earning.kpi.earnings"),
                    data = toSerieData(months, earnings)
                )
            )
        )
    }

    private fun toSerieData(months: List<Int>, earnings: List<Earning>): List<Double> {
        val earningMap = earnings.map { it.date.monthValue to it }.toMap()
        return months.map {
            val earning = earningMap[it]
            if (earning == null) 0.0 else earning.amount.toDouble()
        }
    }

    private fun toDateList(year: Int, months: List<Int>): List<String> {
        return months.map {
            if (it < 10) "$year-0$it" else "$year-$it"
        }
    }
}
