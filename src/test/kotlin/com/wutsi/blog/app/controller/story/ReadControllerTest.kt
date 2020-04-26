package com.wutsi.blog.app.controller.story

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class ReadControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
    }


    @Test
    fun `published story`() {
        gotoPage()

        assertCurrentPageIs(PageName.READ)

        assertElementAttribute(".author img", "src", "https://avatars3.githubusercontent.com/u/39621277?v=4")
        assertElementAttributeEndsWith(".author a", "href", "/@/ray.sponsible")
        assertElementText(".author .bio", "Ray sponsible is a test user")
        assertElementAttribute(".author .facebook", "href", "https://www.facebook.com/ray.sponsible")
        assertElementAttribute(".author .twitter", "href", "https://www.twitter.com/raysponsible")
        assertElementAttribute(".author .linkedin", "href", "https://www.linkedin.com/in/ray.sponsible")
    }

    @Test
    fun `draft story`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        driver.get("$url/read/20/looks-good")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `invalid story`() {
        driver.get("$url/read/9999999/looks-good")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `preview story`() {
        gotoPreview(true)
        assertCurrentPageIs(PageName.READ)
    }

    @Test
    fun `anonymous user cannot preview story`() {
        gotoPreview(false)
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
        gotoPage()

        val url = "http://localhost:8081/read/20/lorem-ipsum"

        assertElementCount(".share .share-facebook", 1)
        assertElementAttribute(".share .share-facebook", "href", "https://www.facebook.com/sharer/sharer.php?display=page&u=$url")
        assertElementAttribute(".share .share-facebook", "target", "_blank")
        assertElementAttribute(".share .share-facebook", "wutsi-track-event", "share")
        assertElementAttribute(".share .share-facebook", "wutsi-track-value", "facebook")
    }

    @Test
    fun `share to Twitter`() {
        gotoPage()

        val url = "http://localhost:8081/read/20/lorem-ipsum"

        assertElementCount(".share .share-twitter", 1)
        assertElementAttribute(".share .share-twitter", "href", "http://www.twitter.com/intent/tweet?url=$url")
        assertElementAttribute(".share .share-twitter", "target", "_blank")
        assertElementAttribute(".share .share-twitter", "wutsi-track-event", "share")
        assertElementAttribute(".share .share-twitter", "wutsi-track-value", "twitter")
    }

    @Test
    fun `share to LinkedIn`() {
        gotoPage()

        val url = "http://localhost:8081/read/20/lorem-ipsum"

        assertElementCount(".share .share-linkedin", 1)
        assertElementAttribute(".share .share-linkedin", "href", "https://www.linkedin.com/shareArticle?mini=true&url=$url")
        assertElementAttribute(".share .share-linkedin", "wutsi-track-event", "share")
        assertElementAttribute(".share .share-linkedin", "wutsi-track-value", "linkedin")
    }

    @Test
    fun `imported content`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story-imported.json")
        gotoPage()

        assertElementPresent(".reader .imported-content-message")
        assertElementAttribute("head link[rel=canonical]", "href", "https://kamerkongossa.cm/2020/01/07/a-yaounde-on-rencontre-le-sous-developpement-par-les-chemins-quon-emprunte-pour-leviter")
    }

    @Test
    fun `META headers`() {
        gotoPage()

        val title = "Lorem Ipsum"
        val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"

        assertElementAttribute("head title", "text", title)
        assertElementAttribute("head meta[name='description']", "content", description)
        assertElementAttribute("head meta[name='robots']", "content", "all")

        assertElementAttribute("head meta[property='og:title']", "content", title)
        assertElementAttribute("head meta[property='og:description']","content", description)
        assertElementAttribute("head meta[property='og:type']", "content", "article")
        assertElementAttribute("head meta[property='og:url']", "content", "http://localhost:8081/read/20/lorem-ipsum")
        assertElementAttribute("head meta[property='og:image']", "content", "https://images.pexels.com/photos/2167395/pexels-photo-2167395.jpeg")
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")
        assertElementAttribute("head meta[property='article:author']", "content", "Ray Sponsible")
        assertElementAttributeStartsWith("head meta[property='article:modified_time']", "content", "2020-03-27T")
        assertElementAttributeStartsWith("head meta[property='article:published_time']", "content", "2020-03-27T")
        assertElementCount("head meta[property='article:tag']", 3)
    }

    @Test
    fun `Twitter card summary_large_image`() {
        gotoPage()

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
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story30-no_thumbnail.json")
        gotoPage()

        val title = "Lorem Ipsum"
        val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"

        assertElementNotPresent("head meta[property='og:image']")

        assertElementAttribute("head meta[name='twitter:title']", "content", title)
        assertElementAttribute("head meta[name='twitter:description']","content", description)
        assertElementAttribute("head meta[name='twitter:card']", "content", "summary")
        assertElementAttribute("head meta[name='twitter:site']", "content", "@raysponsible")
        assertElementAttribute("head meta[name='twitter:creator']", "content", "@raysponsible")
    }

    fun gotoPage() {
        driver.get(url)

        click(".post a")
    }

    fun gotoPreview(login: Boolean = true) {
        if (login) {
            login()
        }

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        driver.get("$url/read/20?preview=true")
    }
}
