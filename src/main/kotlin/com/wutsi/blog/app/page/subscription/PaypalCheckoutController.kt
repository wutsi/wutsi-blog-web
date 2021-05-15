package com.wutsi.blog.app.page.subscription

import com.paypal.core.PayPalHttpClient
import com.paypal.orders.AmountBreakdown
import com.paypal.orders.AmountWithBreakdown
import com.paypal.orders.ApplicationContext
import com.paypal.orders.Item
import com.paypal.orders.Money
import com.paypal.orders.OrderRequest
import com.paypal.orders.OrdersCreateRequest
import com.paypal.orders.PurchaseUnitRequest
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.order.OrderApi
import com.wutsi.order.dto.CreateOrderRequest
import com.wutsi.order.dto.Order
import com.wutsi.site.dto.Site
import com.wutsi.subscription.SubscriptionApi
import com.wutsi.subscription.dto.Plan
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PaypalCheckoutController(
    private val requestContext: RequestContext,
    private val subscriptionApi: SubscriptionApi,
    private val orderApi: OrderApi,
    private val paypalClient: PayPalHttpClient
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaypalCheckoutController::class.java)
    }

    @GetMapping("/checkout/paypal")
    fun paypal(
        @RequestParam planId: Long,
        model: Model
    ): String {
        val site = requestContext.site()
        val plan = subscriptionApi.getPlan(planId, site.internationalCurrency).plan
        val user = requestContext.currentUser()!!
        val orderId = createOrder(plan, site, user)
        val url = preparePaypalCheckout(orderId, site, user)

        return "redirect:$url"
    }

    private fun createOrder(plan: Plan, site: Site, user: UserModel): Long {
        val orderId = orderApi.createOrder(
            CreateOrderRequest(
                siteId = site.id,
                productId = plan.id,
                productRateId = plan.convertedRate.id,
                currency = plan.convertedRate.currency,
                description = "${user.fullName} | ${site.displayName}",
                total = plan.convertedRate.yearly,
                customerId = requestContext.currentUser()?.id
            )
        ).orderId

        LOGGER.info("Wutsi Order created. order_id=$orderId")
        return orderId
    }

    private fun preparePaypalCheckout(orderId: Long, site: Site, user: UserModel): String {
        val order = orderApi.getOrder(orderId).order

        val request = OrdersCreateRequest().requestBody(createOrderRequest(order, site, user))
        val result = paypalClient.execute(request).result()
        val url = result.links().find { it.rel() == "approve" }?.href()

        LOGGER.info("Creating Paypal Order. paypal_url=$url")
        return url!!
    }

    private fun createOrderRequest(order: Order, site: Site, user: UserModel): OrderRequest =
        OrderRequest()
            .purchaseUnits(listOf(createPurchaseUnitRequest(order)))
            .checkoutPaymentIntent("CAPTURE")
            .applicationContext(createApplicationContext(site, user))

    private fun createApplicationContext(site: Site, user: UserModel): ApplicationContext =
        ApplicationContext()
            .brandName(site.displayName)
            .returnUrl("${site.websiteUrl}/@/${user.name}/subscribe/success")
            .cancelUrl("${site.websiteUrl}/@/${user.name}/subscribe/cancel")
            .locale(requestContext.currentUser()?.language)
            .shippingPreference("NO_SHIPPING")

    private fun createPurchaseUnitRequest(order: Order): PurchaseUnitRequest =
        PurchaseUnitRequest()
            .amountWithBreakdown(
                AmountWithBreakdown()
                    .currencyCode(order.currency)
                    .value(order.total.toString())
                    .amountBreakdown(
                        AmountBreakdown()
                            .itemTotal(
                                Money()
                                    .value(order.total.toString())
                                    .currencyCode(order.currency)
                            )
                    )
            )
            .description(order.description)
            .invoiceId(order.id.toString())
            .items(listOf(createItem(order)))

    private fun createItem(order: Order): Item =
        Item().name(order.description)
            .quantity("1")
            .sku(order.productId.toString())
            .unitAmount(
                Money()
                    .value(order.total.toString())
                    .currencyCode(order.currency)
            )
}
