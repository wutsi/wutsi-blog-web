package com.wutsi.blog.app.page.subscription

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PaypalExpressCheckoutController {
    var visited: Boolean = false

    @GetMapping("/paypal/express_checkout")
    fun invoke() {
        visited = true
    }
}
