package com.wutsi.blog.app.page.mail

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito.verify
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class MailControllerTest: SeleniumTestSupport() {
    @Test
    fun unsubscribeFromBlog() {
        driver.get("$url/mail/unsubscribe?u=1&email=foo@gmail.com")

        assertElementPresent(".navbar-blog")
        assertElementNotPresent(".navbar-site")
        assertCurrentPageIs(PageName.MAIL_UNSUBSCRIBE)

        verify(newsletterApi).unsubscribe(1L, "foo@gmail.com")
    }


    @Test
    fun unsubscribeFromWutsi() {
        driver.get("$url/mail/unsubscribe?email=foo@gmail.com")

        assertElementNotPresent(".navbar-blog")
        assertElementPresent(".navbar-site")
        assertCurrentPageIs(PageName.MAIL_UNSUBSCRIBE)

        verify(newsletterApi).unsubscribe("foo@gmail.com")
    }
}
