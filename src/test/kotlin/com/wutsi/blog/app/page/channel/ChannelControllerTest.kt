package com.wutsi.blog.app.page.channel

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class ChannelControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/channel", HttpStatus.OK, "v1/channel/create.json")
        stub(HttpMethod.DELETE, "/v1/channel/12", HttpStatus.OK)
    }

    @Test
    fun `connect to channel`() {
        stub(HttpMethod.POST, "/v1/channel/search", HttpStatus.OK, "v1/channel/search_empty.json")

        gotoPage()

        assertElementPresent("#channel-twitter .btn-connect")
        assertElementPresent("#channel-facebook .btn-connect")
    }

    @Test
    fun `anonymous cannot connect page`() {
        driver.get("$url/me/channel")

        assertCurrentPageIs(PageName.LOGIN)
    }


    @Test
    fun `disconnect from channel`() {
        stub(HttpMethod.POST, "/v1/channel/search", HttpStatus.OK, "v1/channel/search.json")

        gotoPage()

        assertElementPresent("#channel-twitter .btn-disconnect")
        assertElementPresent("#channel-facebook .btn-disconnect")

        click("#channel-twitter .btn-disconnect")
        assertCurrentPageIs(PageName.CHANNEL)
    }

    fun gotoPage() {
        login()

        click("nav .nav-item")
        click("#navbar-channels")

        assertCurrentPageIs(PageName.CHANNEL)

    }
}
