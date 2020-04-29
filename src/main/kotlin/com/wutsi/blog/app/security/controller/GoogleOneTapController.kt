package com.wutsi.blog.app.security.controller

import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.config.OAuthConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest


@Controller
@RequestMapping()
class GoogleOneTapController(
        @Qualifier(OAuthConfiguration.GOOGLE_OAUTH_SERVICE) oauth: OAuth20Service
): GoogleLoginController(oauth) {

    @GetMapping("/login/google/onetap")
    @ResponseBody
    fun oneTap(request: HttpServletRequest): Map<String, String> {
        val state = generateState(request)
        val accessToken = request.getParameter("credential")

        val url = getSigninUrl(
                accessToken = accessToken,
                state = state,
                user = loadUser(accessToken)
        )
        return mapOf("url" to url)
    }
}
