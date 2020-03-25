package com.wutsi.blog.app.controller

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.CookieName
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/logout")
class LogoutController(
        requestContext: RequestContext,
        private val clients: OAuth2AuthorizedClientService,
        private val backend: AuthenticationBackend
): AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LogoutController::class.java)
    }

    @GetMapping()
    fun index(request: HttpServletRequest, response: HttpServletResponse): String {
        backendLogout()
        serverLogout(request)
        browserLogout(request, response)
        return "redirect:/"
    }

    private fun backendLogout() {
        try {
            val token = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val client = clients.loadAuthorizedClient<OAuth2AuthorizedClient>(token.authorizedClientRegistrationId, token.name)
            val accessToken = client.accessToken?.tokenValue

            if (accessToken != null) {
                backend.logoutAsync(accessToken)
            }
        } catch (ex: Exception){
            LOGGER.warn("Unable to logout on backend", ex)
        }
    }

    private fun serverLogout(request: HttpServletRequest) {
        request.logout()
    }

    private fun browserLogout (request: HttpServletRequest, response: HttpServletResponse) {
        val cookie = Cookie(CookieName.ACCESS_TOKEN, "")
        cookie.maxAge = 0
        response.addCookie(cookie)
    }


    override fun page() = PageName.LOGOUT
}
