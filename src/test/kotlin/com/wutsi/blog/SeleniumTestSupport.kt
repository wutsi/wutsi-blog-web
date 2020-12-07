package com.wutsi.blog

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.delete
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.wutsi.blog.app.security.config.SecurityConfiguration
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchTopicResponse
import com.wutsi.blog.sdk.NewsletterApi
import com.wutsi.blog.sdk.PinApi
import com.wutsi.blog.sdk.TagApi
import com.wutsi.blog.sdk.TopicApi
import com.wutsi.core.exception.NotFoundException
import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.doThrow
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.Select
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.UUID
import java.util.concurrent.TimeUnit




@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
abstract class SeleniumTestSupport {
    companion object {
        var wiremock: WireMockServer? = null
    }

    @LocalServerPort
    private val port: Int = 0

    protected var url: String = ""

    protected var timeout = 2L

    lateinit protected var driver: WebDriver

    @MockBean protected lateinit var newsletterApi: NewsletterApi
    @MockBean protected lateinit var pinApi: PinApi
    @MockBean protected lateinit var tagApi: TagApi
    @MockBean protected lateinit var topicApi: TopicApi


    protected fun driverOptions(): ChromeOptions {
        val options = ChromeOptions()
        options.addArguments("--disable-web-security")  // To prevent CORS issues
        options.addArguments("--lang=fr");
        if (System.getProperty("headless") == "true") {
            options.addArguments("--headless")
        }
//        options.setCapability("resolution", "1920x1080")
        return options
    }


    @Before
    fun setUp() {
        this.url = "http://localhost:$port"

        this.driver = ChromeDriver(driverOptions())
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS)

        if (wiremock == null) {
            wiremock = WireMockServer()
            wiremock?.start()
        }

        setupWiremock()
        setupSdk()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        wiremock?.resetMappings()
        driver.quit()
    }

    protected fun setupWiremock() {
        stub(HttpMethod.POST, "/v1/auth", HttpStatus.OK, "v1/session/login.json")
        stub(HttpMethod.GET, "/v1/auth/.*", HttpStatus.OK, "v1/session/get-session1.json")

        stub(HttpMethod.POST, "/v1/channel/search", HttpStatus.OK, "v1/channel/search_empty.json")

        stub(HttpMethod.POST, "/v1/comment/count", HttpStatus.OK, "v1/comment/count.json")

        stub(HttpMethod.POST, "/v1/follower/search", HttpStatus.OK, "v1/follower/search-empty.json")

        stub(HttpMethod.POST, "/v1/like/count", HttpStatus.OK, "v1/like/count.json")

        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search.json")
        stub(HttpMethod.POST, "/v1/story/recommend", HttpStatus.OK, "v1/story/recommend.json")
        stub(HttpMethod.POST, "/v1/story/sort", HttpStatus.OK, "v1/story/sort.json")

        stub(HttpMethod.POST, "/v1/track", HttpStatus.OK, "v1/track/push.json")

        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user1.json")
        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")
        stub(HttpMethod.POST, "/v1/user/search", HttpStatus.OK, "v1/user/search.json")

        stub(HttpMethod.POST, "/v1/sort", HttpStatus.OK, "v1/sort/sort.json")
    }

    protected fun setupSdk() {
        givenNoPin()
        givenTopics()
    }

    protected fun givenTopics() {
        val response = SearchTopicResponse(
                topics = listOf(
                        TopicApiFixtures.createTopicDto(100, "art-entertainment"),
                        TopicApiFixtures.createTopicDto(101, "art", 100),

                        TopicApiFixtures.createTopicDto(200, "industry"),
                        TopicApiFixtures.createTopicDto(201, "biotech", 200)
                )
        )
        `when`(topicApi.all()).thenReturn(response)
    }

    protected fun givenPin(userId: Long=1, storyId: Long=21) {
        val pin = PinApiFixtures.createGetPinResponse(userId, storyId)
        `when`(pinApi.get(userId)).thenReturn(pin)
    }

    protected fun givenNoPin() {
        `when`(pinApi.get(anyLong())).thenThrow(NotFoundException("not_found"))
    }


    protected fun navigate(url: String) {
        driver.get(url)
    }
    protected fun login() {
        navigate(url)

        val state = UUID.randomUUID().toString()
        driver.get(
                url + SecurityConfiguration.QA_SIGNIN_PATTERN +
                "?" + SecurityConfiguration.PARAM_STATE + "=" + state
        )
    }

    protected fun stub(
            method: HttpMethod,
            url: String, status:
            HttpStatus, resource: String? = null,
            contentType: String = "application/json",
            queryParams: Map<String, String>? = null
    ) {
        val urlMatch: UrlMatchingStrategy = urlMatching(url)

        var mapping: MappingBuilder? = null
        if (method == HttpMethod.GET){
            mapping = get(urlMatch)
        } else if (method == HttpMethod.POST){
            mapping = post(urlMatch)
        } else if (method == HttpMethod.DELETE){
            mapping = delete(urlMatch)
        } else {
            IllegalArgumentException("method not supported: $method")
        }

        if (queryParams != null) {
            queryParams.keys.forEach {
                mapping?.withQueryParam(it, WireMock.equalTo(queryParams[it]))
            }
        }
        val response = aResponse()
                .withHeader("Content-Type", contentType)
                .withStatus(status.value())
        if (resource != null){
            val bodyStream = SeleniumTestSupport::class.java.getResourceAsStream("/wiremock/$resource")
            val body = IOUtils.toString(bodyStream)
            response.withBody(body)
        }
        stubFor(mapping?.willReturn(response))
    }

    protected fun assertCurrentPageIs(page: String) {
        assertEquals(page, driver.findElement(By.cssSelector("meta[name=wutsi\\:page_name]"))?.getAttribute("content"))
    }

    protected fun assertElementNotPresent(selector: String){
        assertTrue(driver.findElements(By.cssSelector(selector)).size == 0)
    }

    protected fun assertElementPresent(selector: String){
        assertTrue(driver.findElements(By.cssSelector(selector)).size > 0)
    }

    protected fun assertElementText(selector: String, text: String) {
        assertEquals(text, driver.findElement(By.cssSelector(selector)).text)
    }

    protected fun assertElementTextContains(selector: String, text: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).text.contains(text))
    }

    protected fun assertElementCount(selector: String, count: Int) {
        assertEquals(count, driver.findElements(By.cssSelector(selector)).size)
    }

    protected fun assertElementNotVisible(selector: String) {
        assertEquals("none", driver.findElement(By.cssSelector(selector)).getCssValue("display"))
    }

    protected fun assertElementVisible(selector: String) {
        assertFalse("none".equals(driver.findElement(By.cssSelector(selector)).getCssValue("display")))
    }

    protected fun assertElementAttribute(selector: String, name: String, value: String) {
        assertEquals(value, driver.findElement(By.cssSelector(selector)).getAttribute(name))
    }

    protected fun assertElementAttributeStartsWith(selector: String, name: String, value: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).getAttribute(name).startsWith(value))
    }

    protected fun assertElementAttributeEndsWith(selector: String, name: String, value: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).getAttribute(name).endsWith(value))
    }

    protected fun assertElementAttributeContains(selector: String, name: String, value: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).getAttribute(name).contains(value))
    }

    protected fun assertElementHasClass(selector: String, value: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).getAttribute("class").contains(value))
    }

    protected fun assertElementHasNotClass(selector: String, value: String) {
        assertFalse(driver.findElement(By.cssSelector(selector)).getAttribute("class").contains(value))
    }

    protected fun click(selector: String){
        driver.findElement(By.cssSelector(selector)).click()
    }

    protected fun input(selector: String, value: String){
        val by = By.cssSelector(selector)
        driver.findElement(by).clear()
        driver.findElement(by).sendKeys(value)
    }

    protected fun select(selector: String, index: Int){
        val by = By.cssSelector(selector)
        val select = Select(driver.findElement(by))
        select.selectByIndex(index)
    }

}
