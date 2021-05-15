package com.wutsi.blog.app.page.subscription.config

import com.paypal.core.PayPalEnvironment
import com.paypal.core.PayPalHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PaypalConfiguration {
    @Bean
    fun paypalClient(): PayPalHttpClient {
        val clientId = "AVd6GRKZ9A1GQR7UcxadqfC0srM17ksKLwABcblGV72xAjm963GcpqPjCYT7Fd8pbWg8fUD3Bef16SOK"
        val secret = "EDAAywKD2sKieducUAFXKna-MoemX2ur3JtEwNBBMhe3JHDMOBYCjdY_4l327Dch8OYW4WBrOyuLG3y2"
        val env = PayPalEnvironment.Sandbox(clientId, secret)
        return PayPalHttpClient(env)
    }
}
