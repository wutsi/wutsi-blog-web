package com.wutsi.blog.app.page.paypal.service

import com.paypal.core.PayPalEnvironment
import com.paypal.core.PayPalHttpClient
import com.wutsi.site.SiteAttribute
import com.wutsi.site.dto.Site
import org.springframework.stereotype.Service

@Service
class PayPalHttpClientBuilder {
    fun build(site: Site): PayPalHttpClient {
        val clientId = clientId(site)
        val secret = secret(site)
        val environment = environment(site)
        val env = if (environment.equals("live", true))
            PayPalEnvironment.Live(clientId, secret)
        else
            PayPalEnvironment.Sandbox(clientId, secret)
        return PayPalHttpClient(env)
    }

    private fun clientId(site: Site): String? =
        site.attributes.find { it.urn == SiteAttribute.PAYPAL_CLIENT_ID.urn }?.value

    private fun secret(site: Site): String? =
        site.attributes.find { it.urn == SiteAttribute.PAYPAL_SECRET.urn }?.value

    private fun environment(site: Site): String? =
        site.attributes.find { it.urn == SiteAttribute.PAYPAL_ENVIRONMENT.urn }?.value
}
