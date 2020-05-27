package com.wutsi.blog.app.controller.home

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

class PWAControllerTest: SeleniumTestSupport() {
    @Value("\${wutsi.base-url}")
    lateinit private var baseUrl: String

    @Value("\${wutsi.asset-url}")
    lateinit private var assetUrl: String

    @Test
    fun `pwa headers`() {
        driver.get(url)
        assertElementAttributeEndsWith("head link[rel=manifest]", "href", "/manifest.json")
        assertElementAttributeEndsWith("head link[rel=apple-touch-icon]", "href", "/assets/wutsi/img/logo/logo_96x96.png")
        assertElementAttribute("head meta[name=apple-mobile-web-app-status-bar]", "content", "#f8f8f8")
        assertElementAttribute("head meta[name=theme-color]", "content", "#f8f8f8")
        assertElementAttribute("head meta[name=viewport]", "content", "width=device-width, initial-scale=1")

        assertElementPresent("script#pwa-code")
    }

    @Test
    fun `service worker`() {
        driver.get("$url/sw.js")
    }

    @Test
    fun `manifest`() {
        val rest = RestTemplate()
        val response = rest.getForEntity("$url/manifest.json", Manifest::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val manifest = response.body!!
        assertEquals("Wutsi", manifest.name)
        assertEquals("Wutsi", manifest.short_name)
        assertEquals("standalone", manifest.display)
        assertEquals("portrait-primary", manifest.orientation)
        assertEquals("#f8f8f8", manifest.theme_color)
        assertEquals("#f8f8f8", manifest.background_color)
        assertEquals(baseUrl, manifest.start_url)

        assertEquals(2, manifest.icons.size)
        assertEquals("192x192", manifest.icons[0].sizes)
        assertEquals("image/png", manifest.icons[0].type)
        assertEquals("$assetUrl/assets/wutsi/img/logo/logo_192x192.png", manifest.icons[0].src)

        assertEquals("512x512", manifest.icons[1].sizes)
        assertEquals("image/png", manifest.icons[1].type)
        assertEquals("$assetUrl/assets/wutsi/img/logo/logo_512x512.png", manifest.icons[1].src)
    }
}
