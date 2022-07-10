package com.wutsi.blog.app.page.sitemap

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.page.sitemap.model.SitemapModel
import org.junit.jupiter.api.Test
import java.net.URL
import javax.xml.bind.JAXB
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SitemapControllerTest : SeleniumTestSupport() {
    @Test
    fun `sitemap content`() {
        val sitemap = JAXB.unmarshal(URL("$url/sitemap.xml"), SitemapModel::class.java)
        assertEquals(16, sitemap.url.size)

        assertHasUrl("/", sitemap)
        assertHasUrl("/partner", sitemap)
        assertHasUrl("/about", sitemap)
        assertHasUrl("/writers", sitemap)
        assertHasUrl("/@/ray.sponsible", sitemap)
        assertHasUrl("/@/yvon.larose", sitemap)
        assertHasUrl("/@/omam.mbiyick", sitemap)
        assertHasUrl("/@/roger.milla", sitemap)
        assertHasUrl("/@/samuel.etoo", sitemap)
    }

    @Test
    fun `sitemap header`() {
        driver.get(url)

        assertElementAttributeEndsWith("head link[rel='sitemap']", "href", "/sitemap.xml")
    }

    private fun assertHasUrl(url: String, sitemap: SitemapModel) {
        assertNotNull(sitemap.url.find { it.loc.endsWith(url) })
    }
}
