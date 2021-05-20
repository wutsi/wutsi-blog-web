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
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.security.config.SecurityConfiguration
import com.wutsi.blog.client.channel.SearchChannelResponse
import com.wutsi.blog.client.comment.CountCommentResponse
import com.wutsi.blog.client.comment.CreateCommentResponse
import com.wutsi.blog.client.comment.SearchCommentResponse
import com.wutsi.blog.client.follower.SearchFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerResponse
import com.wutsi.blog.client.like.CountLikeResponse
import com.wutsi.blog.client.like.SearchLikeRequest
import com.wutsi.blog.client.like.SearchLikeResponse
import com.wutsi.blog.client.story.SearchTopicResponse
import com.wutsi.blog.client.user.CountUserResponse
import com.wutsi.blog.client.user.GetUserResponse
import com.wutsi.blog.client.user.SearchUserResponse
import com.wutsi.blog.fixtures.CommentApiFixtures
import com.wutsi.blog.fixtures.ContractApiFixture
import com.wutsi.blog.fixtures.FollowerApiFixtures
import com.wutsi.blog.fixtures.PartnerApiFixtures
import com.wutsi.blog.fixtures.PinApiFixtures
import com.wutsi.blog.fixtures.TopicApiFixtures
import com.wutsi.blog.fixtures.UserApiFixtures
import com.wutsi.blog.sdk.ChannelApi
import com.wutsi.blog.sdk.CommentApi
import com.wutsi.blog.sdk.ContractApi
import com.wutsi.blog.sdk.FollowerApi
import com.wutsi.blog.sdk.LikeApi
import com.wutsi.blog.sdk.PartnerApi
import com.wutsi.blog.sdk.PinApi
import com.wutsi.blog.sdk.TagApi
import com.wutsi.blog.sdk.TelegramApi
import com.wutsi.blog.sdk.TopicApi
import com.wutsi.blog.sdk.UserApi
import com.wutsi.core.exception.NotFoundException
import com.wutsi.order.OrderApi
import com.wutsi.site.SiteApi
import com.wutsi.site.SiteAttribute
import com.wutsi.site.dto.Attribute
import com.wutsi.site.dto.GetSiteResponse
import com.wutsi.site.dto.Site
import com.wutsi.subscription.SubscriptionApi
import com.wutsi.subscription.dto.SearchPlanResponse
import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.runner.RunWith
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
import java.util.Date
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

    protected lateinit var driver: WebDriver

    @MockBean
    protected lateinit var channelApi: ChannelApi

    @MockBean
    protected lateinit var commentApi: CommentApi

    @MockBean
    protected lateinit var contractApi: ContractApi

    @MockBean
    protected lateinit var followerApi: FollowerApi

    @MockBean
    protected lateinit var likeApi: LikeApi

    @MockBean
    protected lateinit var partnerApi: PartnerApi

    @MockBean
    protected lateinit var pinApi: PinApi

    @MockBean
    protected lateinit var tagApi: TagApi

    @MockBean
    protected lateinit var telegramApi: TelegramApi

    @MockBean
    protected lateinit var topicApi: TopicApi

    @MockBean
    protected lateinit var userApi: UserApi

    @MockBean
    protected lateinit var subscriptionApi: SubscriptionApi

    @MockBean
    protected lateinit var siteApi: SiteApi

    @MockBean
    protected lateinit var orderApi: OrderApi

    protected fun driverOptions(): ChromeOptions {
        val options = ChromeOptions()
        options.addArguments("--disable-web-security") // To prevent CORS issues
        options.addArguments("--lang=en")
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

        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search.json")
        stub(HttpMethod.POST, "/v1/story/recommend", HttpStatus.OK, "v1/story/recommend.json")
        stub(HttpMethod.POST, "/v1/story/sort", HttpStatus.OK, "v1/story/sort.json")
    }

    protected fun setupSdk() {
        givenNoChannel()
        givenNoComment()
        givenNoContract()
        givenNoFollower()
        givenNoLike()
        givenNoPin()
        givenTopics()

        givenUser(1, name = "ray.sponsible", fullName = "Ray Sponsible", blog = true)
        givenUser(3, name = "roger.milla", fullName = "Roger Milla", blog = true, biography = "Just the best african soccer player ever!")
        givenUser(99, name = "john.smith", fullName = "John Smith", blog = false)
        givenUser(6666, name = "ze.god", superUser = true, blog = false)
        givenSearchReturn5()

        val site = createSite()
        doReturn(SearchPlanResponse()).whenever(subscriptionApi).partnerPlans(any(), any())
        doReturn(GetSiteResponse(site)).whenever(siteApi).get(any())
    }

    protected fun createSite() = Site(
        id = 1L,
        name = "wutsi",
        displayName = "Wutsi",
        websiteUrl = "http://localhost:8081",
        currency = "XAF",
        internationalCurrency = "EUR",
        language = "fr",
        domainName = "localhost",
        attributes = listOf(
            Attribute(urn = SiteAttribute.PAYPAL_ENABLED.urn, "true")
        )
    )

    protected fun givenNoUser(userId: Long) {
        doThrow(NotFoundException("user_not_found")).whenever(userApi).get(userId)
    }

    protected fun givenNoUser(username: String) {
        doThrow(NotFoundException("user_not_found")).whenever(userApi).get(username)
    }

    protected fun givenSearchReturn5() {
        val users = listOf(
            UserApiFixtures.createUserSummaryDto(1, "ray.sponsible", "Ray Sponsible"),
            UserApiFixtures.createUserSummaryDto(2, "yvon.larose", "Yvon Larose"),
            UserApiFixtures.createUserSummaryDto(3, "roger.milla", "Roger Milla"),
            UserApiFixtures.createUserSummaryDto(4, "omam.mbiyick", "Omam Mbiyick"),
            UserApiFixtures.createUserSummaryDto(5, "samuel.etoo", "Samuel Etoo", blog = false)
        )
        doReturn(SearchUserResponse(users = users)).whenever(userApi).search(any())
        doReturn(CountUserResponse(total = 5)).whenever(userApi).count(any())
    }

    protected fun givenUser(
        userId: Long,
        name: String = "ray.sponsible",
        fullName: String = "Ray Sponsible",
        superUser: Boolean = false,
        blog: Boolean = false,
        biography: String = UserApiFixtures.DEFAULT_BIOGRAPHY
    ) {
        val response = GetUserResponse(
            user = UserApiFixtures.createUserDto(userId, name, fullName, superUser = superUser, blog = blog, biography = biography)
        )
        doReturn(response).whenever(userApi).get(userId)
        doReturn(response).whenever(userApi).get(name)
    }

    protected fun givenNoChannel() {
        doReturn(SearchChannelResponse()).whenever(channelApi).search(any())
        doThrow(NotFoundException("channel_not_found")).whenever(channelApi).get(any())
    }

    protected fun givenNoContract() {
        doThrow(NotFoundException("contract_not_found")).whenever(contractApi).get(any())
    }

    protected fun givenContract(userId: Long) {
        doReturn(ContractApiFixture.createGetContractResponse(userId)).whenever(contractApi).get(1)
    }

    protected fun givenNoFollower() {
        doReturn(SearchFollowerResponse()).whenever(followerApi).search(any())
    }

    protected fun givenUserFollow(userId: Long, followerUserId: Long) {
        val response = FollowerApiFixtures.createSearchFollowerResponse(userId, followerUserId)
        doReturn(response).whenever(followerApi).search(SearchFollowerRequest(followerUserId = followerUserId, userId = userId))
        doReturn(response).whenever(followerApi).search(SearchFollowerRequest(followerUserId = followerUserId))
    }

    protected fun givenNoPartner() {
        doThrow(NotFoundException("partner_not_found")).whenever(partnerApi).get(any())
    }

    protected fun givenPartner() {
        val partner = PartnerApiFixtures.createPartnerDto(
            country = "CM",
            mobileNumber = "664032997",
            fullName = "Ray Sponsible",
            userId = 1,
            email = "ray.sponsible@gmail.com"
        )
        val response = PartnerApiFixtures.createGetPartnerResponse(partner)
        doReturn(response).whenever(partnerApi).get(any())
    }

    protected fun givenNoPin() {
        doThrow(NotFoundException("pin_not_found")).whenever(pinApi).get(any())
    }

    protected fun givenPin(userId: Long = 1, storyId: Long = 21, creationDate: Date = Date()) {
        val pin = PinApiFixtures.createGetPinResponse(userId, storyId, creationDate)
        doReturn(pin).whenever(pinApi).get(userId)
    }

    protected fun givenNoLike() {
        doReturn(SearchLikeResponse()).whenever(likeApi).search(any<SearchLikeRequest>())
        doReturn(CountLikeResponse()).whenever(likeApi).count(any<SearchLikeRequest>())
    }

    protected fun givenNoComment() {
        doReturn(SearchCommentResponse()).whenever(commentApi).search(any())
        doReturn(CountCommentResponse()).whenever(commentApi).count(any())
        doReturn(CreateCommentResponse(commentId = System.currentTimeMillis())).whenever(commentApi).create(any())
    }

    protected fun givenComments(storyId: Long, count: Int) {
        val range = 1..count
        val search = SearchCommentResponse(
            comments = range.map {
                CommentApiFixtures.createCommentDto(
                    userId = it.toLong(),
                    storyId = storyId
                )
            }.toList()
        )
        doReturn(search).whenever(commentApi).search(any())

        val response = CountCommentResponse(
            counts = listOf(
                CommentApiFixtures.createCommentCountDto(
                    storyId = storyId,
                    value = count.toLong()
                )
            )
        )
        doReturn(response).whenever(commentApi).count(any())
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
        doReturn(response).whenever(topicApi).all()
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
        url: String,
        status: HttpStatus,
        resource: String? = null,
        contentType: String = "application/json",
        queryParams: Map<String, String>? = null
    ) {
        val urlMatch: UrlMatchingStrategy = urlMatching(url)

        var mapping: MappingBuilder? = null
        if (method == HttpMethod.GET) {
            mapping = get(urlMatch)
        } else if (method == HttpMethod.POST) {
            mapping = post(urlMatch)
        } else if (method == HttpMethod.DELETE) {
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
        if (resource != null) {
            val bodyStream = SeleniumTestSupport::class.java.getResourceAsStream("/wiremock/$resource")
            val body = IOUtils.toString(bodyStream)
            response.withBody(body)
        }
        stubFor(mapping?.willReturn(response))
    }

    protected fun assertCurrentPageIs(page: String) {
        assertEquals(page, driver.findElement(By.cssSelector("meta[name=wutsi\\:page_name]"))?.getAttribute("content"))
    }

    protected fun assertElementNotPresent(selector: String) {
        assertTrue(driver.findElements(By.cssSelector(selector)).size == 0)
    }

    protected fun assertElementPresent(selector: String) {
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

    protected fun assertElementAttribute(selector: String, name: String, value: String?) {
        if (value == null)
            assertNull(driver.findElement(By.cssSelector(selector)).getAttribute(name))
        else
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

    protected fun click(selector: String) {
        driver.findElement(By.cssSelector(selector)).click()
    }

    protected fun input(selector: String, value: String) {
        val by = By.cssSelector(selector)
        driver.findElement(by).clear()
        driver.findElement(by).sendKeys(value)
    }

    protected fun select(selector: String, index: Int) {
        val by = By.cssSelector(selector)
        val select = Select(driver.findElement(by))
        select.selectByIndex(index)
    }
}
