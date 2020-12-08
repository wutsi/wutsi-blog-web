package com.wutsi.blog.app.component.announcement

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "wutsi.toggles.announcement=false"
    ]
)
class AnnouncementControllerToggleOFFTest : SeleniumTestSupport() {
    @Test
    fun `HTML elements`() {
        driver.get(url)

        assertElementNotPresent("script#wutsi-announcement-js")
        assertElementNotPresent("#wutsi-announcement")
    }
}
