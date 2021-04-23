package com.wutsi.blog.app.page.stats

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.stats.StatsApi
import com.wutsi.stats.dto.SearchStoryKpiResponse
import com.wutsi.stats.dto.SearchStoryTrafficResponse
import com.wutsi.stats.dto.SearchUserKpiResponse
import com.wutsi.stats.dto.SearchUserTrafficResponse
import com.wutsi.stats.dto.StoryKpi
import com.wutsi.stats.dto.UserKpi
import com.wutsi.stats.dto.UserTraffic
import org.junit.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.LocalDate

class StatsUserControllerTest : SeleniumTestSupport() {
    @MockBean
    private lateinit var api: StatsApi

    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        val storyKpi = StoryKpi(
            value = 20,
            date = LocalDate.now()
        )
        doReturn(SearchStoryKpiResponse(listOf(storyKpi))).whenever(api).storyMonthlyKpis(any(), any(), any(), any(), any(), any())
        doReturn(SearchStoryKpiResponse(listOf(storyKpi))).whenever(api).storyDailyKpis(any(), any(), any(), any(), any(), any())

        val userKpi = UserKpi(
            value = 30,
            date = LocalDate.now()
        )
        doReturn(SearchUserKpiResponse(listOf(userKpi))).whenever(api).userMonthlyKpis(any(), any(), any(), any(), any(), any())

        val userTraffic = UserTraffic(
            value = 50,
            date = LocalDate.now(),
            name = "seo"
        )
        doReturn(SearchUserTrafficResponse(listOf(userTraffic))).whenever(api).userMonthlyTraffic(any(), any(), any(), any(), any())

        doReturn(SearchStoryTrafficResponse(listOf())).whenever(api).storyMonthlyTraffic(any(), any(), any(), any(), any())
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
