package com.wutsi.blog.app.page.blog

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.fixtures.UserApiFixtures
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class BlogControllerTest : SeleniumTestSupport() {
    override fun setupSdk() {
        super.setupSdk()

        givenUserFollow(5, 1)
    }

    @Test
    fun `my blog page`() {
        gotoPage(true)

        assertElementText(".author-card h1", "Ray Sponsible")
        assertElementText(".author-card .bio", UserApiFixtures.DEFAULT_BIOGRAPHY)
        assertElementAttribute(".author-card .facebook", "href", "https://www.facebook.com/ray.sponsible")
        assertElementAttribute(".author-card .twitter", "href", "https://www.twitter.com/ray.sponsible")
        assertElementAttribute(".author-card .linkedin", "href", "https://www.linkedin.com/in/ray.sponsible")
        assertElementAttribute(".author-card .youtube", "href", "https://www.youtube.com/channel/ray.sponsible")
        assertElementNotPresent("#alert-no-social-link")

        assertElementCount("#my-stories .story-card", 7)
//        assertElementCount("#following-stories .story-summary-card", 7)
        assertElementCount("#latest-stories .story-summary-card", 3)
    }

    @Test
    fun `my empty blog page`() {
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-empty.json")
        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count-0.json")

        gotoPage(true)

        assertElementCount("#my-stories .story-card", 0)
    }

    @Test
    fun `anonymous blog page`() {
        stub(HttpMethod.POST, "/v1/story/sort", HttpStatus.OK, "v1/story/sort.json")

        gotoPage()

        assertElementText(".author-card h1", "Ray Sponsible")
        assertElementText(".author-card .bio", UserApiFixtures.DEFAULT_BIOGRAPHY)
        assertElementAttribute(".author-card .facebook", "href", "https://www.facebook.com/ray.sponsible")
        assertElementAttribute(".author-card .twitter", "href", "https://www.twitter.com/ray.sponsible")
        assertElementAttribute(".author-card .linkedin", "href", "https://www.linkedin.com/in/ray.sponsible")
        assertElementAttribute(".author-card .youtube", "href", "https://www.youtube.com/channel/ray.sponsible")
        assertElementNotPresent("#alert-no-social-link")

        assertElementCount("#my-stories .story-card", 7)
        assertElementCount("#following-stories .story-summary-card", 0)
        assertElementCount("#latest-stories .story-summary-card", 4)
    }

    @Test
    fun `META headers`() {
        gotoPage()

        val title = "Ray Sponsible"

        assertElementAttribute("html", "lang", "en")
        assertElementAttribute("head title", "text", "$title | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", UserApiFixtures.DEFAULT_BIOGRAPHY)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

        assertElementAttribute("head meta[property='og:title']", "content", title)
        assertElementAttribute("head meta[property='og:description']", "content", UserApiFixtures.DEFAULT_BIOGRAPHY)
        assertElementAttribute("head meta[property='og:type']", "content", "profile")
        assertElementAttribute("head meta[property='og:url']", "content", "http://localhost:8081/@/ray.sponsible")
        assertElementAttribute("head meta[property='og:image']", "content", "https://avatars3.githubusercontent.com/u/39621277?v=4")
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/@/ray.sponsible/rss")
    }

    @Test
    fun `Schemas script`() {
        gotoPage()

        assertElementPresent("script[type='application/ld+json']")
    }

    fun gotoPage(login: Boolean = false) {
        if (login) {
            login()
        }
        driver.get("$url/@/ray.sponsible")

        assertCurrentPageIs(PageName.BLOG)
    }
}
