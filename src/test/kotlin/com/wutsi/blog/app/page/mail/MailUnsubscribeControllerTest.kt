package com.wutsi.blog.app.page.mail

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test

import org.junit.Assert.*
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class MailUnsubscribeControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()
        stub(HttpMethod.POST, "/v1/mail/unsubscribe", HttpStatus.OK, "v1/mail/unsubscribe.json")
    }

    @Test
    fun unsubscribeFromBlog() {
        driver.get("$url/mail/unsubscribe?u=1&email=foo@gmail.com")

        assertElementPresent(".navbar-blog")
        assertElementNotPresent(".navbar-site")
        assertCurrentPageIs(PageName.MAIL_UNSUBSCRIBE)
    }


    @Test
    fun unsubscribeFromWutsi() {
        driver.get("$url/mail/unsubscribe?email=foo@gmail.com")

        assertElementNotPresent(".navbar-blog")
        assertElementPresent(".navbar-site")
        assertCurrentPageIs(PageName.MAIL_UNSUBSCRIBE)
    }
}
