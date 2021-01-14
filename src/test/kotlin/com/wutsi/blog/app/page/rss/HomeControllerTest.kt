package com.wutsi.blog.app.page.rss

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test

class HomeControllerTest : SeleniumTestSupport() {
    @Test
    fun `home page`() {
        driver.get(url)

        assertElementCount(".author-carousel div.author", 3)
        assertElementCount(".story-carousel div.story-card", 3)
        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun `home page META headers`() {
        driver.get(url)
        assertElementAttribute("html", "lang", "en")
        assertElementAttribute("head title", "text", "Wutsi")
        assertElementPresent("head meta[name='description']")
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")
        assertElementAttributeEndsWith("head meta[property='og:image']", "content", "/assets/wutsi/img/logo/logo_512x512.png")

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/rss")
    }

    @Test
    fun `Google Ad-Sense`() {
        driver.get(url)
        assertElementPresent("script#ad-sense-code")
        assertElementAttribute("script#ad-sense-code", "data-ad-client", "test-ad-sense")
    }

    @Test
    fun `home page Google Analytics`() {
        driver.get(url)
        assertElementPresent("script#ga-code")
    }

    @Test
    fun `home page Facebook Pixel`() {
        driver.get(url)
        assertElementPresent("script#fb-pixel-code")
    }

    @Test
    fun `Schemas script`() {
        driver.get(url)
        assertElementPresent("script[type='application/ld+json']")
    }
}
