package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import com.wutsi.blog.app.security.config.OAuthConfiguration
import com.wutsi.blog.app.security.config.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest


@Controller
@RequestMapping("/login/twitter")
class TwitterLoginController(
        logger: KVLogger,
        objectMapper: ObjectMapper,
        @Qualifier(OAuthConfiguration.TWITTER_OAUTH_SERVICE) private val oauth: OAuth10aService
) : AbstractOAuthLoginController(logger, objectMapper) {
    override fun getAuthorizationUrl(request: HttpServletRequest) = oauth.getAuthorizationUrl(oauth.requestToken)


    override fun getError(request: HttpServletRequest): String? {
        return request.getParameter("denied")
    }

    override fun getSigninUrl(request: HttpServletRequest): String {
        val accessToken = request.getParameter("oauth_token")
        val state = generateState(request)
        val user = toOAuthUser(request)
        return getSigninUrl(accessToken, state, user)
    }

    override fun toOAuthUser(attrs: Map<String, Any>) = OAuthUser(
            id = attrs["id"].toString(),
            fullName = attrs["name"].toString(),
            email = attrs["email"]?.toString(),
            pictureUrl = "https://graph.facebook.com/" + attrs["id"] + "/picture?type=square",
            provider = SecurityConfiguration.PROVIDER_TWITTER
        )


    private fun toOAuthUser(request: HttpServletRequest): OAuthUser {
        val response = fetchUser(request)
        logger.add("OAuthUser", response.body)

        val attrs = objectMapper.readValue(response.body, Map::class.java) as Map<String, Any>
        return toOAuthUser(attrs)
    }

    private fun fetchUser(request: HttpServletRequest): Response {
        val verifier = request.getParameter("oauth_verifier")
        val accessToken = oauth.getAccessToken(oauth.requestToken, verifier)

        val oauthRequest = OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json")
        oauth.signRequest(accessToken, oauthRequest)

        return oauth.execute(oauthRequest)
    }
}
