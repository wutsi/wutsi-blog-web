package com.wutsi.blog.app.page.comment

import com.wutsi.blog.SeleniumMobileTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource


@TestPropertySource(
        properties = [
            "wutsi.toggles.comment=false"
        ]
)
class CommentControllerToggleOffTest: SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/comment/search", HttpStatus.OK, "v1/comment/search.json")
        stub(HttpMethod.POST, "/v1/comment", HttpStatus.OK, "v1/comment/create.json")

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")

        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")

        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")
    }

    @Test
    fun `home page not showing comment count` () {
        driver.get(url)

        Thread.sleep(1000)
        assertElementNotPresent("#comment-count-20")
        assertElementNotPresent("#comment-count-21")
        assertElementNotPresent("#comment-count-22")
        assertElementNotPresent("#comment-count-23")
        assertElementNotPresent("#comment-count-24")
        assertElementNotPresent("#comment-count-25")
        assertElementNotPresent("#comment-count-26")
    }

    @Test
    fun `blog page not showing comment count` () {
        driver.get("$url/@/ray.sponsible")

        Thread.sleep(1000)
        assertElementNotPresent("#comment-count-20")
        assertElementNotPresent("#comment-count-21")
        assertElementNotPresent("#comment-count-22")
        assertElementNotPresent("#comment-count-23")
        assertElementNotPresent("#comment-count-24")
        assertElementNotPresent("#comment-count-25")
        assertElementNotPresent("#comment-count-26")
    }

    @Test
    fun `reader without the comment widget`() {
        gotoPage()

        Thread.sleep(1000)
        assertElementNotPresent(".comment-widget")
    }

    fun gotoPage(login: Boolean = false) {
        if (login){
            login()
        }

        driver.get(url)
        click(".post a")
    }
}
