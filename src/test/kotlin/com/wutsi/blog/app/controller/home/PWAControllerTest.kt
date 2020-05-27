package com.wutsi.blog.app.controller.home

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test

class PWAControllerTest: SeleniumTestSupport() {
    @Test
    fun `pwa headers`() {
        driver.get(url)
        assertElementAttributeEndsWith("head link[rel='manifest']", "href", "/assets/wutsi/manifest.json")
        assertElementAttributeEndsWith("head link[rel='apple-touch-icon']", "href", "/assets/wutsi/img/logo/logo_512x512.png")
        assertElementAttributeEndsWith("head link[rel='apple-touch-icon']", "href", "/assets/wutsi/img/logo/logo_512x512.png")
        assertElementAttribute("head meta[name='apple-mobile-web-app-status-bar']", "content", "#f8f8f8")
        assertElementAttribute("head meta[name='theme-color']", "content", "#f8f8f8")
        assertElementAttribute("head meta[name='viewport']", "content", "width=device-width, initial-scale=1")

        assertElementPresent("script#pwa-code")
    }

    @Test
    fun `service worker`() {
        driver.get("$url/sw.js")
    }

    @Test
    fun `manifest`() {
        driver.get("$url/manifest.json")
    }
}
