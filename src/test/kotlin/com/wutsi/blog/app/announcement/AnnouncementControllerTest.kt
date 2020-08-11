package com.wutsi.blog.app.announcement

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test

class AnnouncementControllerTest: SeleniumTestSupport() {
    @Test
    fun `HTML elements`() {
        driver.get(url)

        assertElementPresent("script#wutsi-announcement-js")
        assertElementPresent("#wutsi-announcement")
    }
}
