package com.wutsi.blog.app.security.oauth.controller

import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.config.OAuthConfiguration
import com.wutsi.blog.app.security.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest


@Controller
@RequestMapping("/login/facebook")
class FacebookLoginController(
        @Qualifier(OAuthConfiguration.FACEBOOK_OAUTH_SERVICE) private val oauth: OAuth20Service
) : AbstractOAuth20LoginController() {
    override fun getOAuthService() = oauth

    override fun loadUser(accessToken: String): OAuthUser {
        val request = OAuthRequest(Verb.GET, "https://api.github.com/user")
        oauth.signRequest(accessToken, request)

        val response = oauth.execute(request)
        val attrs = objectMapper.readValue(response.body, Map::class.java) as Map<String, Any>
        return OAuthUser(
                id = attrs["id"].toString(),
                name = attrs["id"].toString(),
                fullName = attrs["name"].toString(),
                email = attrs["email"]?.toString(),
                pictureUrl = "http://graph.facebook.com/" + attrs["id"] + "/picture?type=normal",
                provider = SecurityConfiguration.PROVIDER_FACEBOOK
        )
    }

    override fun getError(request: HttpServletRequest) = request.getParameter("error_code")
}
