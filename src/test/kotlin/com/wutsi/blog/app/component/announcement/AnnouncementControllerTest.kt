package com.wutsi.blog.app.component.announcement

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test

class AnnouncementControllerTest: SeleniumTestSupport() {
    @Test
    fun `HTML elements`() {
        login()

        Thread.sleep(1000)
        assertElementPresent("script#wutsi-announcement-js")
        assertElementPresent("#wutsi-announcement")
    }
}
