package com.wutsi.blog.app.page.subscription

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test

class SubscriptionSuccessControllerTest : SeleniumTestSupport() {
    @Test
    fun index() {
        navigate("$url/@/ray.sponsible/subscribe/success")

        assertCurrentPageIs(PageName.SUBSCRIPTION_SUCCESS)
    }
}
