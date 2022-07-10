package com.wutsi.blog.app.page.follower

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.jupiter.api.Test

class FollowControllerTest : SeleniumMobileTestSupport() {
    @Test
    fun `Users are redirecte to subscribe page`() {
        driver.get("$url/@/ray.sponsible/follow")
        assertCurrentPageIs(PageName.SUBSCRIPTION)
    }
}
