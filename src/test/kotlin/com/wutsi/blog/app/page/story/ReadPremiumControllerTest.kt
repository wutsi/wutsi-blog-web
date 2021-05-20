package com.wutsi.blog.app.page.story

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.subscription.dto.Plan
import com.wutsi.subscription.dto.SearchPlanResponse
import com.wutsi.subscription.dto.SearchSubscriptionResponse
import com.wutsi.subscription.dto.Subscription
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class ReadPremiumControllerTest : SeleniumMobileTestSupport() {
    private val plan = Plan(1)

    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")
    }

    override fun setupSdk() {
        super.setupSdk()

        doReturn(SearchPlanResponse(listOf(plan))).whenever(subscriptionApi).partnerPlans(any(), any())
        doReturn(SearchSubscriptionResponse(listOf())).whenever(subscriptionApi).partnerSubscriptions(any(), any(), any())
    }

    @Test
    fun `story with PREMIUM SUSCRIBER access are viewed partially by anonymous users`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99-access-premium.json")

        driver.get("$url/read/20/test")

        assertElementCount("#story-paywall", 1)
    }

    @Test
    fun `story with PREMIUM SUSCRIBER access are viewed partially by non premium`() {
        login()
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99-access-premium.json")

        driver.get("$url/read/20/test")

        assertElementCount("#story-paywall", 1)
    }

    @Test
    fun `story with PREMIUM SUSCRIBER access are viewed completely by premium`() {
        login()

        val subscription = Subscription(plan = plan)
        doReturn(SearchSubscriptionResponse(listOf(subscription))).whenever(subscriptionApi).partnerSubscriptions(any(), any(), any())
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99-access-premium.json")

        driver.get("$url/read/20/test")

        assertElementCount("#story-paywall", 0)
    }
}
