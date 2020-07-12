package com.wutsi.blog.app.security

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
        logger: KVLogger
) {
    @GetMapping()
    fun login (request: HttpServletRequest): String {
        val requestToken = oauth.getRequestToken()
        val url = oauth.getAuthorizationUrl(requestToken)
        return "redirect:$url"
    }

    @GetMapping("/callback")
    open fun callback(request: HttpServletRequest): String {
        return ""
    }
}
