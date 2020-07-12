package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth1AccessToken
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
    override fun getAuthorizationUrl(request: HttpServletRequest): String {
        val requestToken = oauth.requestToken
        logger.add("requestToken", requestToken.token)
        return oauth.getAuthorizationUrl(requestToken)
    }


    override fun getError(request: HttpServletRequest): String? {
        return request.getParameter("denied")
    }

    override fun getSigninUrl(request: HttpServletRequest): String {
        val requestToken = oauth.requestToken
        logger.add("requestToken", requestToken.token)

        val verifier = request.getParameter("oauth_verifier")
        val accessToken = oauth.getAccessToken(requestToken, verifier)
        val user = toOAuthUser(accessToken)
        val state = generateState(request)
        return getSigninUrl(accessToken.token, state, user)
    }

    override fun toOAuthUser(attrs: Map<String, Any>) = OAuthUser(
            id = attrs["id"].toString(),
            fullName = attrs["name"].toString(),
            email = attrs["email"]?.toString(),
            pictureUrl = "https://graph.facebook.com/" + attrs["id"] + "/picture?type=square",
            provider = SecurityConfiguration.PROVIDER_TWITTER
        )


    private fun toOAuthUser(accessToken: OAuth1AccessToken): OAuthUser {
        val response = fetchUser(accessToken)
        logger.add("OAuthUser", response.body)

        val attrs = objectMapper.readValue(response.body, Map::class.java) as Map<String, Any>
        return toOAuthUser(attrs)
    }

    private fun fetchUser(accessToken: OAuth1AccessToken): Response {
        val oauthRequest = OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json")
        oauth.signRequest(accessToken, oauthRequest)
        return oauth.execute(oauthRequest)
    }
}
