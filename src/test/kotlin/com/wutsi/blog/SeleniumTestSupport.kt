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
import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.runner.RunWith
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.Select
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.TimeUnit


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
abstract class SeleniumTestSupport {
    companion object {
        protected var wiremock: WireMockServer? = null
    }

    @LocalServerPort
    private val port: Int = 0

    protected var url: String = ""

    protected var timeout = 10L

    protected var driver: WebDriver? = null


    @Before
    fun setUp() {
        this.url = "http://localhost:$port"

        val options = ChromeOptions()
        if (System.getProperty("headless") == "true") {
            options.addArguments("--headless")
        }

        this.driver = ChromeDriver(options)
        driver!!.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS)

        if (wiremock == null) {
            wiremock = WireMockServer()
            wiremock?.start()
        }

        setupWiremock()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        wiremock?.resetMappings()
        driver?.quit()
    }

    protected fun setupWiremock() {
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
        assertEquals(page, driver?.findElement(By.cssSelector("meta[name=wutsi\\:page_name]"))?.getAttribute("content"))
    }

    protected fun assertElementNotPresent(selector: String){
        assertTrue(driver!!.findElements(By.cssSelector(selector)).size == 0)
    }

    protected fun assertElementPresent(selector: String){
        assertTrue(driver!!.findElements(By.cssSelector(selector)).size > 0)
    }

    protected fun assertElementText(selector: String, text: String) {
        assertEquals(text, driver!!.findElement(By.cssSelector(selector)).text)
    }

    protected fun assertElementCount(selector: String, count: Int) {
        assertEquals(count, driver!!.findElements(By.cssSelector(selector)).size)
    }

    protected fun assertElementAttribute(selector: String, name: String, value: String) {
        assertEquals(value, driver!!.findElement(By.cssSelector(selector)).getAttribute(name))
    }

    protected fun click(selector: String){
        driver!!.findElement(By.cssSelector(selector)).click()
    }

    protected fun input(selector: String, value: String){
        val by = By.cssSelector(selector)
        driver!!.findElement(by).clear()
        driver!!.findElement(by).sendKeys(value)
    }

    protected fun select(selector: String, index: Int){
        val by = By.cssSelector(selector)
        val select = Select(driver!!.findElement(by))
        select.selectByIndex(index)
    }

}
