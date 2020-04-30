package com.wutsi.blog.app.security.controller

import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.config.OAuthConfiguration
import com.wutsi.blog.app.security.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.core.http.Http
import com.wutsi.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest


@Controller
@RequestMapping("/login/onetap")
class OneTapController(
        logger: KVLogger,
        @Qualifier(OAuthConfiguration.GOOGLE_OAUTH_SERVICE) oauth: OAuth20Service,

        private val http: Http
): GoogleLoginController(logger, oauth) {

    @GetMapping("/callback")
    @ResponseBody
    override fun callback(request: HttpServletRequest): String {
        val state = generateState(request)
        val credential = request.getParameter("credential")
        val user = toOAuthUser(credential)
        val url = getSigninUrl(credential , state, user)

        logger.add("RedirectURL", url)
        return url
    }

    override fun toOAuthUser(credential: String): OAuthUser {
        val url = "https://oauth2.googleapis.com/tokeninfo?id_token=$credential"
        val response = http.get(url, Map::class.java).body
        logger.add("TokenInfo", response?.toString())

        return OAuthUser(
                id = toString(response["id"]),
                fullName = toString(response["name"]),
                pictureUrl = toString(response["picture"]),
                email = toString(response["email"]),
                provider = SecurityConfiguration.PROVIDER_GOOGLE
        )
    }

    private fun toString(value: Any?): String = if (value == null) "" else value.toString()
}
