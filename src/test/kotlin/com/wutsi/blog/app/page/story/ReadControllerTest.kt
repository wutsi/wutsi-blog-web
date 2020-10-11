package com.wutsi.blog.app.page.story

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class ReadControllerTest: SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")

        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `story menu available for story owner`() {
        login()
        driver.get("$url/read/20/test")

        assertElementPresent("#story-menu")
    }

    @Test
    fun `story menu not available for non-story owner`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99.json")
        login()
        driver.get("$url/read/20/test")

        assertElementNotPresent("#story-menu")
    }

    @Test
    fun `story menu not available for anonymous`() {
        driver.get("$url/read/20/looks-good")
        assertElementNotPresent("#story-menu")
    }


    @Test
    fun `story tracking not available for story owner`() {
        login()
        driver.get("$url/read/20/test")

        assertElementNotPresent("#track-script")
    }

    @Test
    fun `story tracking available for non-story owner`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99.json")
        login()
        driver.get("$url/read/20/test")

        assertElementPresent("#track-script")
    }

    @Test
    fun `story tracking available for anonymous`() {
        driver.get("$url/read/20/test")

        assertElementPresent("#track-script")
    }



    @Test
    fun `published story`() {
        driver.get("$url/read/20/test")

        assertCurrentPageIs(PageName.READ)

        assertElementAttribute(".author img", "src", "https://avatars3.githubusercontent.com/u/39621277?v=4")
        assertElementAttributeEndsWith(".author a", "href", "/@/ray.sponsible")
        assertElementText(".author .bio", "Ray sponsible is a test user")

        assertElementAttribute(".author .website", "href", "https://www.me.com/ray.sponsible")
        assertElementAttribute(".author .website", "wutsi-track-event", "link")
        assertElementAttribute(".author .website", "wutsi-track-value", "website")

        assertElementAttribute(".author .facebook", "href", "https://www.facebook.com/ray.sponsible")
        assertElementAttribute(".author .facebook", "wutsi-track-event", "link")
        assertElementAttribute(".author .facebook", "wutsi-track-value", "facebook")

        assertElementAttribute(".author .twitter", "href", "https://www.twitter.com/ray.sponsible")
        assertElementAttribute(".author .twitter", "wutsi-track-event", "link")
        assertElementAttribute(".author .twitter", "wutsi-track-value", "twitter")

        assertElementAttribute(".author .linkedin", "href", "https://www.linkedin.com/in/ray.sponsible")
        assertElementAttribute(".author .linkedin", "wutsi-track-event", "link")
        assertElementAttribute(".author .linkedin", "wutsi-track-value", "linkedin")

        assertElementText("h1", "Lorem Ipsum")
        assertElementText("h2.tagline", "This is awesome story!")

        assertElementNotPresent("#super-user-banner")
        assertElementNotPresent("#story-menu")
    }

    @Test
    fun `draft story`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        driver.get("$url/read/20/looks-good")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `not-live story`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-not-live.json")
        driver.get("$url/read/20/looks-good")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `invalid story`() {
        driver.get("$url/read/9999999/looks-good")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `share to Social Network`() {
        driver.get("$url/read/20/looks-good")

        click("#share-menu")

        val url = "http://localhost:8081/read/20/lorem-ipsum"
        assertElementAttribute(".dropdown .share-facebook", "href", "javascript: wutsi.share('facebook')")
        assertElementAttribute(".dropdown .share-twitter", "href", "javascript: wutsi.share('twitter')")
        assertElementAttribute(".dropdown .share-whatsapp", "href", "javascript: wutsi.share('whatsapp')")
        assertElementAttribute(".dropdown .share-messenger", "href", "javascript: wutsi.share('messenger')")
        assertElementAttribute(".dropdown .share-linkedin", "href", "javascript: wutsi.share('linkedin')")
    }

    @Test
    fun `imported content`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story-imported.json")
        driver.get("$url/read/20/looks-good")

        assertElementAttribute("head link[rel=canonical]", "href", "https://kamerkongossa.cm/2020/01/07/a-yaounde-on-rencontre-le-sous-developpement-par-les-chemins-quon-emprunte-pour-leviter")
    }

    @Test
    fun `META headers`() {
        driver.get("$url/read/20/looks-good")

        val title = "Lorem Ipsum"
        val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"

        assertElementAttribute("html", "lang", "fr")
        assertElementAttribute("head title", "text", "$title | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", description)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

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

        assertElementAttribute("head meta[name='wutsi:story_id']", "content", "20")
        assertElementPresent("head meta[name='wutsi:hit_id']")

        assertElementAttribute("head meta[name='facebook:app_id']", "content", "629340480740249")
    }

    @Test
    fun `Twitter card summary_large_image`() {
        driver.get("$url/read/20/looks-good")

        val title = "Lorem Ipsum"
        val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"

        assertElementAttribute("head meta[name='twitter:title']", "content", title)
        assertElementAttribute("head meta[name='twitter:description']","content", description)
        assertElementAttribute("head meta[name='twitter:card']", "content", "summary_large_image")
        assertElementAttribute("head meta[name='twitter:site']", "content", "@ray.sponsible")
        assertElementAttribute("head meta[name='twitter:creator']", "content", "@ray.sponsible")
    }

    @Test
    fun `Twitter card summary`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story30-no_thumbnail.json")
        driver.get("$url/read/20/looks-good")

        val title = "Lorem Ipsum"
        val description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry"

        assertElementNotPresent("head meta[property='og:image']")

        assertElementAttribute("head meta[name='twitter:title']", "content", title)
        assertElementAttribute("head meta[name='twitter:description']","content", description)
        assertElementAttribute("head meta[name='twitter:card']", "content", "summary")
        assertElementAttribute("head meta[name='twitter:site']", "content", "@ray.sponsible")
        assertElementAttribute("head meta[name='twitter:creator']", "content", "@ray.sponsible")
    }

    @Test
    fun `show recommendations when available`() {
        driver.get("$url/read/20/looks-good")

        Thread.sleep(1000)
        assertElementCount("#recommendation-container .post", 7)
        assertElementAttribute("#recommendation-container .post a", "wutsi-track-event", "xread")

        assertElementCount("#recommendation-container .btn-read-more", 1)
        assertElementAttribute("#recommendation-container .btn-read-more", "wutsi-track-event", "xread-more")
        assertElementAttributeEndsWith("#recommendation-container .btn-read-more", "href", "/")
    }

    @Test
    fun `show no recommendations when none are available`() {
        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search-none.json")
        driver.get("$url/read/20/looks-good")

        Thread.sleep(5000)
        assertElementCount("#recommendation-container .post", 0)

        assertElementCount("#recommendation-container .btn-read-more", 1)
        assertElementAttribute("#recommendation-container .btn-read-more", "wutsi-track-event", "xread-more")
        assertElementAttributeEndsWith("#recommendation-container .btn-read-more", "href", "/")
    }

    @Test
    fun `show no recommendations on backend errors`() {
        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.INTERNAL_SERVER_ERROR)
        driver.get("$url/read/20/looks-good")

        Thread.sleep(5000)
        assertElementCount("#recommendation-container .post", 0)
    }

    @Test
    fun `Google Ad-Sense`() {
        driver.get("$url/read/20/looks-good")
        assertElementPresent("script#ad-sense-code")
        assertElementAttribute("script#ad-sense-code", "data-ad-client", "test-ad-sense")
    }

    @Test
    fun `Google Analytics`() {
        driver.get("$url/read/20/looks-good")
        assertElementPresent("script#ga-code")
    }

    @Test
    fun `Facebook Pixel`() {
        driver.get("$url/read/20/looks-good")
        assertElementPresent("script#fb-pixel-code")
    }

    @Test
    fun `Schemas script`() {
        driver.get("$url/read/20/test")

        assertElementPresent("script[type='application/ld+json']")
    }
}
