package com.wutsi.blog.app.page.stats

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.stats.dto.KpiType
import com.wutsi.stats.dto.SearchStoryKpiResponse
import com.wutsi.stats.dto.SearchStoryTrafficResponse
import com.wutsi.stats.dto.SearchUserKpiResponse
import com.wutsi.stats.dto.SearchUserTrafficResponse
import com.wutsi.stats.dto.StoryKpi
import com.wutsi.stats.dto.UserKpi
import com.wutsi.stats.dto.UserTraffic
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.LocalDate

class StatsStoryControllerTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        val storyKpi = StoryKpi(
            storyId = 20,
            value = 20,
            date = LocalDate.now(),
            type = KpiType.VIEWER.name
        )
        doReturn(SearchStoryKpiResponse(listOf(storyKpi))).whenever(statsApi).storyMonthlyKpis(any(), any(), any(), any(), any(), any(), any())
        doReturn(SearchStoryKpiResponse(listOf(storyKpi))).whenever(statsApi).storyDailyKpis(any(), any(), any(), any(), any(), any(), any())

        val userKpi = UserKpi(
            value = 30,
            date = LocalDate.now(),
            type = KpiType.VIEWER.name
        )
        doReturn(SearchUserKpiResponse(listOf(userKpi))).whenever(statsApi).userMonthlyKpis(any(), any(), any(), any(), any(), any())

        val userTraffic = UserTraffic(
            value = 50,
            date = LocalDate.now(),
            name = "seo"
        )
        doReturn(SearchUserTrafficResponse(listOf(userTraffic))).whenever(statsApi).userMonthlyTraffic(any(), any(), any(), any(), any())

        doReturn(SearchStoryTrafficResponse(listOf())).whenever(statsApi).storyMonthlyTraffic(any(), any(), any(), any(), any())
    }

    @Test
    fun `owner can view his stats`() {
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
        Thread.sleep(1000)
        click(".story:first-child .dropdown .btn")
        click(".story .menu-item-stats")
    }
}
