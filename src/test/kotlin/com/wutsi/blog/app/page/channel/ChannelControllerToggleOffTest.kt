package com.wutsi.blog.app.page.channel

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.channel=false"
        ]
)
class ChannelControllerToggleOffTest: SeleniumTestSupport() {
    @Test
    fun `channel menu not available`() {
        login()

        click("nav .nav-item")
        assertElementNotPresent("#navbar-channels")
    }

    @Test
    fun `channel page not accessible`() {
        login()
        driver.get("$url/me/channel")

        assertCurrentPageIs(PageName.ERROR_404)
    }
}
