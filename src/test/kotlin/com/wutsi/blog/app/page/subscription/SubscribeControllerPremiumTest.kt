package com.wutsi.blog.app.page.subscription

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.paypal.core.PayPalHttpClient
import com.paypal.http.HttpResponse
import com.paypal.orders.LinkDescription
import com.paypal.orders.Order
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.page.paypal.service.PayPalHttpClientBuilder
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.follower.CreateFollowerResponse
import com.wutsi.blog.client.follower.SearchFollowerResponse
import com.wutsi.order.dto.CreateOrderResponse
import com.wutsi.order.dto.GetOrderResponse
import com.wutsi.subscription.dto.GetPlanResponse
import com.wutsi.subscription.dto.Plan
import com.wutsi.subscription.dto.PlanRate
import com.wutsi.subscription.dto.SearchPlanResponse
import com.wutsi.subscription.dto.SearchSubscriptionResponse
import com.wutsi.subscription.dto.Subscription
import org.junit.Assert.assertTrue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import java.time.LocalDate

class SubscribeControllerPremiumTest : SeleniumTestSupport() {
    @LocalServerPort
    lateinit var port: Integer

    @Autowired
    lateinit var paypal: PaypalExpressCheckoutController

    @MockBean
    lateinit var paypalBuilder: PayPalHttpClientBuilder

    lateinit var paypalClient: PayPalHttpClient

    override fun setupSdk() {
        super.setupSdk()

        paypalClient = mock()
        doReturn(paypalClient).whenever(paypalBuilder).build(any())

        paypal.visited = false
    }

    @Test
    fun `Premium - cannot subscribe if user is not a blog`() {
        login()
        navigate("$url/@/john.smith/subscribe?premium=1")
        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `Premium - cannot subscribe if the blog do not have subscription plan`() {
        login()
        navigate("$url/@/ray.sponsible/subscribe?premium=1")
        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `Premium - cannot subscription if the current user has a subscription plan`() {
        val plan = createPlan()
        doReturn(SearchPlanResponse(listOf(plan))).whenever(subscriptionApi).partnerPlans(any(), any())

        val subscription = createSubscription()
        doReturn(SearchSubscriptionResponse(listOf(subscription))).whenever(subscriptionApi).partnerSubscriptions(any(), any(), any())

        login()
        navigate("$url/@/ray.sponsible/subscribe?premium=1")
        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `Premium - subscribe with PayPal`() {
        val plan = createPlan()
        doReturn(SearchPlanResponse(listOf(plan))).whenever(subscriptionApi).partnerPlans(any(), any())
        doReturn(GetPlanResponse(plan)).whenever(subscriptionApi).getPlan(any(), any())

        doReturn(SearchSubscriptionResponse(emptyList())).whenever(subscriptionApi).partnerSubscriptions(any(), any(), any())

        val order = createOrder(11)
        doReturn(CreateOrderResponse(11)).whenever(orderApi).createOrder(any())
        doReturn(GetOrderResponse(order)).whenever(orderApi).getOrder(any())

        val paypalOrder = createPaypalOrder()
        val response = mock<HttpResponse<Order>>()
        doReturn(paypalOrder).whenever(response).result()
        doReturn(response).whenever(paypalClient).execute<HttpResponse<Order>>(any())

        login()
        navigate("$url/@/ray.sponsible/subscribe?premium=1")
        assertCurrentPageIs(PageName.SUBSCRIPTION)

        click("#btn-paypal-checkout")

        assertTrue(paypal.visited)
    }

    @Test
    fun `Premium - follow the blog when there are no plan for authenticated user`() {
        doReturn(SearchPlanResponse(emptyList())).whenever(subscriptionApi).partnerPlans(any(), any())
        doReturn(SearchFollowerResponse()).whenever(followerApi).search(any())

        login()
        navigate("$url/@/roger.milla/subscribe?premium=1")
        assertCurrentPageIs(PageName.SUBSCRIPTION)

        doReturn(CreateFollowerResponse(11)).whenever(followerApi).create(any())
        click(".btn-follow")
        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `Premium - show login buttons when there are no plan for anonymous user`() {
        doReturn(SearchPlanResponse(emptyList())).whenever(subscriptionApi).partnerPlans(any(), any())

        navigate("$url/@/roger.milla/subscribe?premium=1")
        assertCurrentPageIs(PageName.SUBSCRIPTION)

        assertElementCount("#btn-google", 1)
    }

    private fun createSubscription() = Subscription(
        id = 1,
        siteId = 1,
        planId = 1,
        orderId = 111,
        status = "ACTIVE",
        startDate = LocalDate.now().minusDays(1),
        endDate = LocalDate.now().plusYears(1)
    )

    private fun createPlan() = Plan(
        id = 1,
        name = "Premium",
        description = "This is the description",
        siteId = 1,
        active = true,
        partnerId = 1,
        rate = PlanRate(
            id = 11,
            yearly = 50000,
            monthly = 5000,
            currency = "XAF"
        ),
        convertedRate = PlanRate(
            id = 11,
            yearly = 75,
            monthly = 8,
            currency = "EUR"
        )
    )

    private fun createPaypalOrder() = Order()
        .links(
            mutableListOf(
                LinkDescription()
                    .rel("approve")
                    .href("http://localhost:$port/paypal/express_checkout")
            )
        )

    private fun createOrder(id: Long) = com.wutsi.order.dto.Order(
        id = id
    )
}
