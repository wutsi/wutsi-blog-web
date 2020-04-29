package com.wutsi.blog.app.security.controller

import com.github.scribejava.core.model.OAuthConstants.CLIENT_ID
import com.github.scribejava.core.oauth.OAuth20Service
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.wutsi.blog.app.config.OAuthConfiguration
import com.wutsi.blog.app.security.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.Collections
import javax.servlet.http.HttpServletRequest


@Controller
@RequestMapping("/login/onetap")
class OneTapController(
        logger: KVLogger,
        @Qualifier(OAuthConfiguration.GOOGLE_OAUTH_SERVICE) oauth: OAuth20Service,

        private val transport: HttpTransport,
        private val jsonFactory: JsonFactory
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
        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build()

        val idToken = verifier.verify(credential)
        logger.add("Token", idToken)

        val payload = idToken.getPayload()
        return OAuthUser(
                id = payload.subject,
                fullName = payload.get("name") as String,
                pictureUrl = payload.get("picture") as String,
                email = payload.getEmail(),
                provider = SecurityConfiguration.PROVIDER_GOOGLE
        )
    }

}
