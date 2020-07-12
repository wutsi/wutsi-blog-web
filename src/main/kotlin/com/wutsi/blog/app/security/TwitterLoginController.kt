package com.wutsi.blog.app.security

import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import com.wutsi.blog.app.security.config.OAuthConfiguration
import com.wutsi.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest






@Controller
@RequestMapping("/login/twitter")
class TwitterLoginController(
        @Qualifier(OAuthConfiguration.TWITTER_OAUTH_SERVICE) private val oauth: OAuth10aService,
        private val logger: KVLogger
) {
    @GetMapping()
    fun login (request: HttpServletRequest): String {
        val requestToken = oauth.getRequestToken()
        val url = oauth.getAuthorizationUrl(requestToken)
        return "redirect:$url"
    }

    @GetMapping("/callback")
    fun callback(request: HttpServletRequest): String {
        val requestToken = oauth.getRequestToken()
        val accessToken = oauth.getAccessToken(requestToken, "123")

        val request = OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json")
        oauth.signRequest(accessToken, request)

        val response = oauth.execute(request)
        logger.add("response", response.body)

        return ""
    }
}
