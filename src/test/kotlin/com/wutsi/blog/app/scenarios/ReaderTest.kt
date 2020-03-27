package com.wutsi.blog.app.scenarios

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class ReaderTest: SeleniumTestSupport() {
    companion object {
        private const val PUBLISHED_ID = "10"
        private const val DRAFT_ID = "20"
    }

    override fun setupWiremock() {
        stub(HttpMethod.GET, "/v1/story/$PUBLISHED_ID", HttpStatus.OK, "v1/story/get_published.json")
        stub(HttpMethod.GET, "/v1/story/$DRAFT_ID", HttpStatus.OK, "v1/story/get_draft.json")

        stub(HttpMethod.GET, "/v1/user/[0-9]+", HttpStatus.OK, "v1/user/get.json")
    }


    @Test
    fun `user should read published story`() {
        driver?.get("$url/read/$PUBLISHED_ID/looks-good")

        assertCurrentPageIs(PageName.READ)
    }

    @Test
    fun `user should be redirected to 404 when he try to read draft story`() {
        driver?.get("$url/read/$DRAFT_ID/looks-good")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `user should be redirected to 404 when he try to read invalid story`() {
        driver?.get("$url/read/9999999/looks-good")

        assertCurrentPageIs(PageName.ERROR_404)
    }
}
