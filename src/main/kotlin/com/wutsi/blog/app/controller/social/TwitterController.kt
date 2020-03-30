package com.wutsi.blog.app.controller.social

import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth10aService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/twitter")
class TwitterController {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TwitterController::class.java)
    }

    @Value ("\${wutsi.auth.twitter.client-id}")
    private lateinit var clientId: String

    @Value ("\${wutsi.auth.twitter.client-secret}")
    private lateinit var clientSecret: String

    @GetMapping("/login")
    fun login (): String {
        val service : OAuth10aService = ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .build(TwitterApi.instance())
        val requestToken = service.requestToken
        val url = service.getAuthorizationUrl(requestToken)
        return "redirect:$url"
    }

    @GetMapping("/callback")
    fun callack(request: HttpServletRequest) {
        LOGGER.info("$request")
    }
}
