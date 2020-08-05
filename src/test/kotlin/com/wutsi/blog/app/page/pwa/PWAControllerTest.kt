package com.wutsi.blog.app.page.pwa

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.page.pwa.model.ManifestModel
import com.wutsi.blog.app.util.PWAHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class PWAControllerTest: SeleniumTestSupport() {
    @Value("\${wutsi.base-url}")
    lateinit private var baseUrl: String

    @Value("\${wutsi.asset-url}")
    lateinit private var assetUrl: String

    @Value("\${wutsi.pwa.firebase.sender-id}")
    lateinit private var senderId: String

    val rest = RestTemplate()

    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }


    @Test
    fun `pwa headers`() {
        driver.get(url)
        assertElementAttributeEndsWith("head link[rel=manifest]", "href", "/manifest-${PWAHelper.VERSION}.json")
        assertElementAttributeEndsWith("head link[rel=apple-touch-icon]", "href", "/assets/wutsi/img/logo/logo_96x96.png")
        assertElementAttribute("head meta[name=apple-mobile-web-app-status-bar]", "content", "#f8f8f8")
        assertElementAttribute("head meta[name=apple-mobile-web-app-title]", "content", "Wutsi")
        assertElementAttribute("head meta[name=apple-mobile-web-app-capable]", "content", "yes")
        assertElementAttribute("head meta[name=mobile-web-app-capable]", "content", "yes")
        assertElementAttribute("head meta[name=theme-color]", "content", "#f8f8f8")
        assertElementAttribute("head meta[name=viewport]", "content", "width=device-width, initial-scale=1")

        assertElementPresent("script#pwa-js")
    }

    @Test
    fun `service worker`() {
        val response = rest.getForEntity("$url/sw-${PWAHelper.VERSION}.js", String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertHeaderEquals(response,"Content-Type", "application/javascript;charset=UTF-8")
    }

    @Test
    fun `add to homescreen script`() {
        val response = rest.getForEntity("$url/a2hs-${PWAHelper.VERSION}.js", String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertHeaderEquals(response,"Content-Type", "application/javascript;charset=UTF-8")
    }

    @Test
    fun `add to homescreen in homepage`() {
        driver.get(url)

        Thread.sleep(1000)
        assertElementPresent("script#a2hs-js")
        assertElementPresent("#a2hs-container")
    }

    @Test
    fun `add to homescreen in reader`() {
        driver.get("$url/read/20/test")

        Thread.sleep(1000)
        assertElementPresent("script#a2hs-js")
        assertElementPresent("#a2hs-container")
    }

    @Test
    fun `push notification in homepage`() {
        driver.get(url)

        Thread.sleep(1000)
        assertElementPresent("script#firebase-app-js")
        assertElementPresent("script#firebase-messaging-js")
        assertElementPresent("script#firebase-js")
        assertElementPresent("#push-container")
    }

    @Test
    fun `push notification in reader`() {
        driver.get("$url/read/20/test")

        Thread.sleep(1000)
        assertElementPresent("script#firebase-app-js")
        assertElementPresent("script#firebase-messaging-js")
        assertElementPresent("script#firebase-js")
        assertElementPresent("#push-container")
    }

    @Test
    fun `manifest`() {
        val response = rest.getForEntity("$url/manifest-${PWAHelper.VERSION}.json", ManifestModel::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val manifest = response.body!!
        assertEquals("Wutsi", manifest.name)
        assertEquals("Wutsi", manifest.short_name)
        assertEquals("standalone", manifest.display)
        assertEquals("portrait-primary", manifest.orientation)
        assertEquals("#f8f8f8", manifest.theme_color)
        assertEquals("#f8f8f8", manifest.background_color)
        assertEquals("$baseUrl?utm_medium=pwa&utm_source=app", manifest.start_url)
        assertEquals(senderId, manifest.gcm_sender_id)

        assertEquals(2, manifest.icons.size)
        assertEquals("192x192", manifest.icons[0].sizes)
        assertEquals("image/png", manifest.icons[0].type)
        assertEquals("$assetUrl/assets/wutsi/img/logo/logo_192x192.png", manifest.icons[0].src)

        assertEquals("512x512", manifest.icons[1].sizes)
        assertEquals("image/png", manifest.icons[1].type)
        assertEquals("$assetUrl/assets/wutsi/img/logo/logo_512x512.png", manifest.icons[1].src)
    }

    private fun assertHeaderEquals(response: ResponseEntity<String>, header: String, value: String) {
        val values = response.headers[header]
        assertEquals(true, values?.contains(value))
    }


}
