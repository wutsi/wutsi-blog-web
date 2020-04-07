package com.wutsi.blog.app.scenarios

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class LogoutControllerTest: SeleniumTestSupport() {
    companion object {
        private const val PUBLISHED_ID = "10"
        private const val DRAFT_ID = "20"
        private const val PUBLISHED_NO_THUMBNAIL_ID = "30"
    }

    override fun setupWiremock() {
        stub(HttpMethod.POST, "/v1/auth", HttpStatus.OK, "v1/session/login.json")
        stub(HttpMethod.GET, "/v1/auth/.*", HttpStatus.OK, "v1/session/get-session1.json")

        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search.json")

        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user1.json")
        stub(HttpMethod.POST, "/v1/user/search", HttpStatus.OK, "v1/user/search.json")
    }


    @Test
    fun `logout`() {
        login()

        driver.get("$url/logout")
        assertCurrentPageIs(PageName.HOME)
    }
}
