package com.wutsi.blog.app.controller

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class ReadControllerTest: SeleniumTestSupport() {
    companion object {
        private const val PUBLISHED_ID = "10"
        private const val DRAFT_ID = "20"
        private const val PUBLISHED_NO_THUMBNAIL_ID = "30"
    }

    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/$PUBLISHED_ID", HttpStatus.OK, "v1/story/get-story10-published.json")
        stub(HttpMethod.GET, "/v1/story/$PUBLISHED_NO_THUMBNAIL_ID", HttpStatus.OK, "v1/story/get-story30-no_thumbnail.json")
        stub(HttpMethod.GET, "/v1/story/$DRAFT_ID", HttpStatus.OK, "v1/story/get-story20-draft.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
    }


    @Test
    fun `published story`() {
        driver.get("$url/read/$PUBLISHED_ID/looks-good")

        assertCurrentPageIs(PageName.READ)
    }

    @Test
    fun `draft story`() {
        driver.get("$url/read/$DRAFT_ID/looks-good")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `invalid story`() {
        driver.get("$url/read/9999999/looks-good")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `preview story`() {
        login()

        driver.get("$url/read/$DRAFT_ID?preview=true")
        assertCurrentPageIs(PageName.READ)
    }

    @Test
    fun `anonymous user cannot preview story`() {
        driver.get("$url/read/$DRAFT_ID?preview=true")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `only owner can preview story`() {
        login()
        driver.get("$url/read/99?preview=true")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `share to Facebook`() {
        driver.get("$url/read/$PUBLISHED_ID/looks-good")

        val url = "http://localhost:8081/read/$PUBLISHED_ID/lorem-ipsum"

        assertElementCount(".share .share-facebook", 2)
        assertElementAttribute(".share .share-facebook", "href", "https://www.facebook.com/sharer/sharer.php?display=page&u=$url")
        assertElementAttribute(".share .share-facebook", "target", "_blank")
    }

    @Test
    fun `share to Twitter`() {
        driver.get("$url/read/$PUBLISHED_ID/lorem-ipsum")

        val url = "http://localhost:8081/read/$PUBLISHED_ID/lorem-ipsum"

        assertElementCount(".share .share-twitter", 2)
        assertElementAttribute(".share .share-twitter", "href", "http://www.twitter.com/intent/tweet?url=$url")
        assertElementAttribute(".share .share-facebook", "target", "_blank")
    }

    @Test
    fun `share to LinkedIn`() {
        driver.get("$url/read/$PUBLISHED_ID/lorem-ipsum")

        val url = "http://localhost:8081/read/$PUBLISHED_ID/lorem-ipsum"

        assertElementCount(".share .share-linkedin", 2)
        assertElementAttribute(".share .share-linkedin", "href", "https://www.linkedin.com/shareArticle?mini=true&url=$url")
    }

    @Test
    fun `META headers`() {
        driver.get("$url/read/$PUBLISHED_ID/looks-good")

        val title = "Lorem Ipsum"
        val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"

        assertElementAttribute("head title", "text", title)
        assertElementAttribute("head meta[name='description']", "content", description)
        assertElementAttribute("head meta[name='robots']", "content", "all")

        assertElementAttribute("head meta[property='og:title']", "content", title)
        assertElementAttribute("head meta[property='og:description']","content", description)
        assertElementAttribute("head meta[property='og:type']", "content", "article")
        assertElementAttribute("head meta[property='og:url']", "content", "http://localhost:8081/read/10/lorem-ipsum")
        assertElementAttribute("head meta[property='og:image']", "content", "https://images.pexels.com/photos/2167395/pexels-photo-2167395.jpeg")
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")
        assertElementAttribute("head meta[property='article:author']", "content", "Ray Sponsible")
        assertElementAttributeStartsWith("head meta[property='article:modified_time']", "content", "2020-03-27T")
        assertElementAttributeStartsWith("head meta[property='article:published_time']", "content", "2020-03-27T")
        assertElementCount("head meta[property='article:tag']", 3)
    }

    @Test
    fun `Twitter card summary_large_image`() {
        driver.get("$url/read/$PUBLISHED_ID/looks-good")

        val title = "Lorem Ipsum"
        val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"

        assertElementAttribute("head meta[name='twitter:title']", "content", title)
        assertElementAttribute("head meta[name='twitter:description']","content", description)
        assertElementAttribute("head meta[name='twitter:card']", "content", "summary_large_image")
        assertElementAttribute("head meta[name='twitter:site']", "content", "@raysponsible")
        assertElementAttribute("head meta[name='twitter:creator']", "content", "@raysponsible")
    }

    @Test
    fun `Twitter card summary`() {
        driver.get("$url/read/$PUBLISHED_NO_THUMBNAIL_ID/looks-good")

        val title = "Lorem Ipsum"
        val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"

        assertElementAttribute("head meta[name='twitter:title']", "content", title)
        assertElementAttribute("head meta[name='twitter:description']","content", description)
        assertElementAttribute("head meta[name='twitter:card']", "content", "summary")
        assertElementAttribute("head meta[name='twitter:site']", "content", "@raysponsible")
        assertElementAttribute("head meta[name='twitter:creator']", "content", "@raysponsible")
    }
}
