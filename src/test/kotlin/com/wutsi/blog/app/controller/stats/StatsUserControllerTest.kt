package com.wutsi.blog.app.controller.stats

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StatsUserControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.POST, "/v1/stats/search", HttpStatus.OK, "v1/stats/search.json")
        stub(HttpMethod.POST, "/v1/stats/search/user", HttpStatus.OK, "v1/stats/search_user.json")
        stub(HttpMethod.POST, "/v1/stats/search/story", HttpStatus.OK, "v1/stats/search_story.json")
    }


    @Test
    fun `user can view his stats`() {
        gotoPage()
        assertCurrentPageIs(PageName.STATS_USER)
    }

    @Test
    fun `anonymous cannot view stats`() {
        driver.get("$url/stats")

        assertCurrentPageIs(PageName.LOGIN)
    }

    fun gotoPage() {
        login()
        click("nav .nav-item")

        click("#navbar-stats")
    }

}
