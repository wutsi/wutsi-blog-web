package com.wutsi.blog.app.security.controller

import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.security.config.OAuthConfiguration
import com.wutsi.blog.app.security.config.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/login/google")
class GoogleLoginController(
        logger: KVLogger,
        @Qualifier(OAuthConfiguration.GOOGLE_OAUTH_SERVICE) private val oauth: OAuth20Service
): AbstractOAuth20LoginController(logger) {
    override fun getOAuthService() = oauth

    override fun getUserUrl() = "https://www.googleapis.com/oauth2/v3/userinfo"

    override fun toOAuthUser(attrs: Map<String, Any>) = OAuthUser(
                id = attrs["sub"].toString(),
                fullName = attrs["name"].toString(),
                email = attrs["email"]?.toString(),
                pictureUrl = attrs["picture"]?.toString(),
                provider = SecurityConfiguration.PROVIDER_GOOGLE
        )

}
