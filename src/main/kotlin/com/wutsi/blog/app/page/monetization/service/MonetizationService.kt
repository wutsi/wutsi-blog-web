package com.wutsi.blog.app.page.monetization.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.monetization.MonetizationMapper
import com.wutsi.blog.app.page.monetization.model.PlanForm
import com.wutsi.blog.app.page.monetization.model.PlanModel
import com.wutsi.blog.app.page.monetization.model.SubscriptionModel
import com.wutsi.subscription.SubscriptionApi
import com.wutsi.subscription.dto.CreatePlanRequest
import com.wutsi.subscription.dto.Status
import com.wutsi.subscription.dto.UpdatePlanRequest
import org.springframework.stereotype.Service

@Service
class MonetizationService(
    private val api: SubscriptionApi,
    private val mapper: MonetizationMapper,
    private val requestContext: RequestContext
) {
    fun savePlan(form: PlanForm) {
        val yearly = form.yearly.toLong()

        if (form.id == null) {
            api.createPlan(
                request = CreatePlanRequest(
                    siteId = requestContext.siteId(),
                    name = form.name,
                    description = form.description,
                    currency = requestContext.site().currency,
                    partnerId = requestContext.currentUser()?.id,
                    yearlyRate = yearly,
                    monthlyRate = yearly / 12
                )
            )
        } else {
            api.updatePlan(
                planId = form.id,
                request = UpdatePlanRequest(
                    name = form.name,
                    description = form.description,
                    active = true,
                    yearlyRate = yearly,
                    monthlyRate = yearly / 12
                )
            )
        }
    }

    fun deactivatePlan(partnerId: Long) {
        val plan = currentPlan(partnerId)
        if (plan != null) {
            api.updatePlan(
                planId = plan.id,
                request = UpdatePlanRequest(
                    name = plan.name,
                    description = plan.description,
                    active = false,
                    yearlyRate = plan.rate.yearly.value,
                    monthlyRate = plan.rate.monthly.value
                )
            )
        }
    }

    fun currentPlan(partnerId: Long): PlanModel? {
        val plans = api.partnerPlans(partnerId, requestContext.site().internationalCurrency)
            .plans
            .filter { it.active }
            .map { mapper.toPlanModel(it) }

        return if (plans.isEmpty()) null else plans[0]
    }

    fun currentSubscription(partnerId: Long): SubscriptionModel? {
        val subscriberId = requestContext.currentUser()?.id ?: return null
        val subscriptions = api.partnerSubscriptions(partnerId, Status.ACTIVE.name, subscriberId).subscriptions
        return if (subscriptions.isEmpty())
            null
        else
            mapper.toSubscriptionModel(subscriptions[0])
    }
}
