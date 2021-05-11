package com.wutsi.blog.app.page.monetization

import com.wutsi.blog.app.common.model.MoneyModel
import com.wutsi.blog.app.page.monetization.model.PlanModel
import com.wutsi.blog.app.page.monetization.model.PlanRateModel
import com.wutsi.subscription.dto.Plan
import com.wutsi.subscription.dto.PlanRate
import org.springframework.stereotype.Service

@Service
class PlanMapper {
    fun toPlanModel(plan: Plan) = PlanModel(
        id = plan.id,
        name = plan.name,
        description = if (plan.description.isNullOrEmpty()) "" else plan.description,
        rate = toPlanRateModel(plan.rate),
        active = plan.active
    )

    fun toPlanRateModel(rate: PlanRate) = PlanRateModel(
        id = rate.id,
        yearly = MoneyModel(rate.yearly, rate.currency),
        monthly = MoneyModel(rate.monthly, rate.currency)
    )
}
