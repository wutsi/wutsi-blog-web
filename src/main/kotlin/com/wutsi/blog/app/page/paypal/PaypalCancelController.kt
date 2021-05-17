package com.wutsi.blog.app.page.paypal

import com.wutsi.blog.app.common.service.RequestContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PaypalCancelController(
    private val requestContext: RequestContext
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaypalCancelController::class.java)
    }

    @GetMapping("/checkout/paypal/cancel")
    fun paypal(
        @RequestParam token: String,
        @RequestParam(name = "PayerID") payerId: String
    ): String {
        LOGGER.info("PayPal payment failed")

        val user = requestContext.currentUser()!!
        return "redirect:/@/${user.name}/subscribe?error=error.paypal_failed"
    }
}
