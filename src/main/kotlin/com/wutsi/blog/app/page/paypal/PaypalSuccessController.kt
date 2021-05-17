package com.wutsi.blog.app.page.paypal

import com.paypal.core.PayPalHttpClient
import com.paypal.orders.OrderActionRequest
import com.paypal.orders.OrdersCaptureRequest
import com.wutsi.blog.app.common.service.RequestContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PaypalSuccessController(
    private val requestContext: RequestContext,
    private val paypalClient: PayPalHttpClient
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

        val request = OrdersCaptureRequest(token)
        request.requestBody(OrderActionRequest())
        paypalClient.execute(request)

        val user = requestContext.currentUser()!!
        return "redirect:/@/${user.name}/subscribe/success"
    }
}
