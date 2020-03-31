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


@Controller
@RequestMapping("/login/github")
class GithubLoginController(
        @Qualifier(OAuthConfiguration.GITHUB_OAUTH_SERVICE) private val oauth: OAuth20Service
): AbstractOAuth20LoginController() {
    override fun getOAuthService() = oauth

    override fun loadUser(accessToken: String): OAuthUser {
        val request = OAuthRequest(Verb.GET, "https://api.github.com/user")
        oauth.signRequest(accessToken, request)

        val response = oauth.execute(request)
        val attrs = objectMapper.readValue(response.body, Map::class.java) as Map<String, Any>
        return OAuthUser(
                id = attrs["login"].toString(),
                fullName = githubFullName(attrs),
                email = attrs["email"]?.toString(),
                pictureUrl = attrs["avatar_url"]?.toString(),
                provider = SecurityConfiguration.PROVIDER_GITHUB
        )
    }

    private fun githubFullName(attrs: Map<String, Any>): String {
        val name = attrs["name"]?.toString()
        return if (name == null || name.isEmpty()) attrs["login"]!!.toString() else name
    }
}
