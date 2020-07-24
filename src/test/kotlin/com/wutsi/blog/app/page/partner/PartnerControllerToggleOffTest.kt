package com.wutsi.blog.app.page.partner

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.page.sitemap.model.SitemapModel
import com.wutsi.blog.app.util.PageName
import org.junit.Assert.assertNull
import org.junit.Test
import org.springframework.test.context.TestPropertySource
import java.net.URL
import javax.xml.bind.JAXB

@TestPropertySource(
        properties = ["wutsi.toggles.wpp=false"]
)
class PartnerControllerToggleOffTest: SeleniumTestSupport() {
    @Test
    fun partnerMenuOff() {
        login()

        click("nav .nav-item")
        assertElementNotPresent("#navbar-wpp")
    }

    @Test
    fun `partner page not accessible`() {
        login()
        driver.get("$url/partner")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `partner page not in sitemap`() {
        val sitemap = JAXB.unmarshal(URL("$url/sitemap.xml"), SitemapModel::class.java)

        assertHasNotUrl("/partner", sitemap)
    }

    private fun assertHasNotUrl(url: String, sitemap: SitemapModel) {
        assertNull(sitemap.url.find { it.loc.endsWith(url) })
    }
}
