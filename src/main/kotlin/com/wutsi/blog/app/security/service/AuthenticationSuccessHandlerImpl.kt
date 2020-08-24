package com.wutsi.blog.app.security.service

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class AuthenticationSuccessHandlerImpl: AuthenticationSuccessHandler {
    companion object {
        const val REDIRECT_URL_KEY = "com.wutsi.redirect_url_key"
    }

    private val requestCache: RequestCache = HttpSessionRequestCache()

    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        if (response.isCommitted()) {
            return
        }

        val redirectUrl = request.session.getAttribute(REDIRECT_URL_KEY)
        try {
            if (redirectUrl == null) {
                val savedRequest = this.requestCache.getRequest(request, response)
                if (savedRequest != null){
                    response.sendRedirect(savedRequest.getRedirectUrl())
                } else {
                    response.sendRedirect("/")
                }
            } else {
                response.sendRedirect(redirectUrl.toString())
            }
        } finally {
            request.session.removeAttribute(REDIRECT_URL_KEY)
        }
    }
}
