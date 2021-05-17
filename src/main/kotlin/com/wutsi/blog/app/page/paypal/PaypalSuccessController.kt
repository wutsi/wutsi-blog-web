package com.wutsi.blog.app.page.paypal

import com.paypal.orders.OrderActionRequest
import com.paypal.orders.OrdersCaptureRequest
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.paypal.service.PayPalHttpClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PaypalSuccessController(
    private val requestContext: RequestContext,
    private val paypalClientBuilder: PayPalHttpClientBuilder
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaypalSuccessController::class.java)
    }

    @GetMapping("/checkout/paypal/success")
    fun paypal(
        @RequestParam token: String,
        @RequestParam(name = "PayerID") payerId: String
    ): String {
        LOGGER.info("PayPal payment successful. Finalizing...")

        val user = requestContext.currentUser()
        try {
            val site = requestContext.site()
            val request = OrdersCaptureRequest(token)
            request.requestBody(OrderActionRequest())
            paypalClientBuilder.build(site).execute(request)

            return "redirect:/@/${user?.name}/subscribe/success"
        } catch (ex: Exception) {
            LOGGER.error("Payment error", ex)
            return "redirect:/@/${user?.name}/subscribe?error=error.paypal_failed"
        }
    }
}
