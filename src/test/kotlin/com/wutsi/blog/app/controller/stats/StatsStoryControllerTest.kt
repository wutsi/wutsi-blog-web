package com.wutsi.blog.app.controller.stats

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StatsStoryControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.POST, "/v1/stats/search", HttpStatus.OK, "v1/stats/search.json")

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
    }


    @Test
    fun `stats page`() {
        gotoPage()

        assertCurrentPageIs(PageName.STATS_STORY)
    }

    @Test
    fun `anonymous cannot access stats page`() {
        driver.get("$url/stats/story/20")

        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `cannot access stats page of another user story`() {
        login()
        driver.get("$url/stats/story/99")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    fun gotoPage() {
        login()
        click("nav .nav-item")

        click("#navbar-draft")
        click("#tab-published")
        click(".story:first-child .dropdown .btn")
        click(".story .menu-item-stats")
    }

}
