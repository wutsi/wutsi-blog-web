package com.wutsi.blog.app.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.subscription.dto.CreatePlanResponse
import com.wutsi.subscription.dto.Plan
import com.wutsi.subscription.dto.PlanRate
import com.wutsi.subscription.dto.SearchPlanResponse
import com.wutsi.subscription.dto.UpdatePlanResponse
import org.junit.Test

class SettingsMonetizationTest : SeleniumTestSupport() {
    @Test
    fun `setup monetization`() {
        // Goto settings page
        gotoPage()

        // Goto monetization page
        click("#monetization-card-none .btn-setup")
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.MONETIZATION_EDIT)

        // Save monetization information
        val plan = createPlan(true)
        doReturn(SearchPlanResponse(listOf(plan))).whenever(subscriptionApi).partnerPlans(any(), any())
        doReturn(CreatePlanResponse(plan.id)).whenever(subscriptionApi).createPlan(any())

        input("#yearly", plan.rate.yearly.toString())
        input("#description", "This is the description")
        click("#btn-submit")
        Thread.sleep(1000)

        // Back to settings
        assertCurrentPageIs(PageName.SETTINGS)
        assertElementText("#monetization-card .yearly-rate", "50,000 XAF")
    }

    @Test
    fun `deactivate monetization`() {
        // Goto settings page - with monetization setup
        val plan0 = createPlan()
        doReturn(SearchPlanResponse(listOf(plan0))).whenever(subscriptionApi).partnerPlans(any(), any())
        gotoPage()

        // Deactivate
        doReturn(UpdatePlanResponse(plan0.id)).whenever(subscriptionApi).updatePlan(any(), any())
        doReturn(SearchPlanResponse(listOf(createPlan(active = false)))).whenever(subscriptionApi).partnerPlans(any(), any())

        click("#monetization-card .btn-delete")
        driver.switchTo().alert().accept()
        Thread.sleep(1000)

        // Back to settings
        assertCurrentPageIs(PageName.SETTINGS)
        assertElementPresent("#monetization-card-none")
    }

    @Test
    fun `update monetization`() {
        // Goto settings page - with monetization setup
        val plan0 = createPlan()
        doReturn(SearchPlanResponse(listOf(plan0))).whenever(subscriptionApi).partnerPlans(any(), any())
        gotoPage()

        // Goto monetization page
        click("#monetization-card .btn-edit")
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.MONETIZATION_EDIT)

        // Save monetization information
        val plan = createPlan(true, 70000)
        doReturn(SearchPlanResponse(listOf(plan))).whenever(subscriptionApi).partnerPlans(any(), any())
        doReturn(UpdatePlanResponse(plan.id)).whenever(subscriptionApi).updatePlan(any(), any())

        input("#yearly", plan.rate.yearly.toString())
        input("#description", "This is the description of the plan")
        click("#btn-submit")
        Thread.sleep(1000)

        // Back to settings
        assertCurrentPageIs(PageName.SETTINGS)
        assertElementText("#monetization-card .yearly-rate", "70,000 XAF")
    }

    @Test
    fun `update monetization fails will display and error message`() {
        // Goto settings page - with monetization setup
        val plan0 = createPlan()
        doReturn(SearchPlanResponse(listOf(plan0))).whenever(subscriptionApi).partnerPlans(any(), any())
        gotoPage()

        // Goto monetization page
        click("#monetization-card .btn-edit")
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.MONETIZATION_EDIT)

        // Save monetization information
        val plan = createPlan(true, 70000)
        doReturn(SearchPlanResponse(listOf(plan))).whenever(subscriptionApi).partnerPlans(any(), any())
        doThrow(RuntimeException::class).whenever(subscriptionApi).updatePlan(any(), any())

        input("#yearly", plan.rate.yearly.toString())
        input("#description", "This is the description of the plan")
        click("#btn-submit")
        Thread.sleep(1000)

        // Back to settings
        assertCurrentPageIs(PageName.MONETIZATION_EDIT)
        assertElementVisible(".alert")
    }

    private fun gotoPage() {
        login()
        navigate("$url/me/settings")

        assertCurrentPageIs(PageName.SETTINGS)
    }

    private fun createPlan(active: Boolean = true, yearly: Long = 50000, id: Long = 1L) = Plan(
        id = id,
        name = "test",
        description = "This is an example of plan",
        active = active,
        rate = PlanRate(id = 11, yearly = yearly, monthly = 6000, currency = "XAF")
    )
}
