package com.wutsi.blog.app.security

import com.wutsi.blog.app.util.CookieName
import org.springframework.stereotype.Component
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class AccessTokenStorage {
    fun get(request: HttpServletRequest): String? {
        val cookie = getCookie(request)
        return if (cookie == null) null else cookie.value
    }

    fun put(accessToken: String, request: HttpServletRequest, response: HttpServletResponse) {
        var cookie = getCookie(request)
        if (cookie != null) {
            cookie.maxAge = 86400 // 1 days
        } else {
            cookie = Cookie(CookieName.ACCESS_TOKEN, accessToken)
            response.addCookie(cookie)
        }

        cookie.value = accessToken
        cookie.maxAge = 86400 // 1 days
        cookie.path = "/"
        response.addCookie(cookie)
    }

    fun remove(response: HttpServletResponse) {
        val cookie = Cookie(CookieName.ACCESS_TOKEN, "")
        cookie.maxAge = 0
        response.addCookie(cookie)
    }

    private fun getCookie(request: HttpServletRequest): Cookie? {
        val cookies = request.cookies
        if (cookies == null || cookies.size == 0) {
            return null
        }
        return cookies.find { it.name == CookieName.ACCESS_TOKEN }
    }
}
