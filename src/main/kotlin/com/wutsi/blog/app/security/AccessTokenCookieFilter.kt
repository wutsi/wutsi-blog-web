package com.wutsi.blog.app.security

import com.wutsi.blog.app.util.CookieName
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


//@Component
class AccessTokenCookieFilter(
        private val clients: OAuth2AuthorizedClientService
) : OncePerRequestFilter() {

    companion object {
        fun getCookie(request: HttpServletRequest): Cookie? {
            if (request.cookies != null) {
                for (cookie in request.cookies) {
                    if (cookie.name == CookieName.ACCESS_TOKEN) {
                        return cookie
                    }
                }
            }
            return null
        }
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val accessToken = getAccessToken()
        if (accessToken != null) {
            val cookie = getCookie(request)
            if (cookie != null) {
                cookie.maxAge = 86400 // 1 days
                cookie.value = accessToken
                cookie.path = "/"
                response.addCookie(cookie)
            } else {
                val cookie = Cookie(CookieName.ACCESS_TOKEN, accessToken)
                cookie.maxAge = 86400 // 1 days
                cookie.path = "/"
                response.addCookie(cookie)
            }
        }

        chain.doFilter(request, response)
    }

    private fun getAccessToken(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is OAuth2AuthenticationToken){
            val client = clients.loadAuthorizedClient<OAuth2AuthorizedClient>(auth.authorizedClientRegistrationId, auth.name)
            return client.accessToken?.tokenValue
        } else {
            return null
        }
    }
}
