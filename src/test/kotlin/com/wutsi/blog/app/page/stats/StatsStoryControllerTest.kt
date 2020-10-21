package com.wutsi.blog.app.page.stats

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StatsStoryControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.POST, "/v1/stats/search/daily", HttpStatus.OK, "v1/stats/search.json")
        stub(HttpMethod.POST, "/v1/stats/search/monthly/story", HttpStatus.OK, "v1/stats/search_story.json")
        stub(HttpMethod.POST, "/v1/stats/search/monthly/traffic", HttpStatus.OK, "v1/stats/search_traffic.json")

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
    }


    @Test
    fun `owner can view his stats`() {
        gotoPage()
        assertCurrentPageIs(PageName.STATS_STORY)
    }

    @Test
    fun `superuser can view any`() {
        stub(HttpMethod.GET, "/v1/user/.+", HttpStatus.OK, "v1/user/get-superuser.json")
        gotoPage()
        assertCurrentPageIs(PageName.STATS_STORY)
    }


    @Test
    fun `anonymous cannot view stats`() {
        driver.get("$url/stats/story/20")

        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `user cannot view stats of another user`() {
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
