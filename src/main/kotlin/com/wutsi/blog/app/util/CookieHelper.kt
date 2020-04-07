package com.wutsi.blog.app.util

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


object CookieHelper {
    fun get(name: String, request: HttpServletRequest): String? {
        val cookie = getCookie(name, request)
        return if (cookie == null) null else cookie.value
    }

    fun remove(name: String, response: HttpServletResponse) {
        val cookie = Cookie(name, "")
        cookie.maxAge = 0
        response.addCookie(cookie)
    }

    fun put(name: String, value: String?, request: HttpServletRequest, response: HttpServletResponse, maxAge: Int = 86400) {
        var cookie = getCookie(name, request)
        if (cookie == null) {
            cookie = Cookie(name, value)
        }

        cookie.value = value
        cookie.maxAge = maxAge
        cookie.path = "/"
        response.addCookie(cookie)
    }

    private fun getCookie(name: String, request: HttpServletRequest): Cookie? {
        val cookies = request.cookies
        if (cookies == null || cookies.size == 0) {
            return null
        }
        return cookies.find { it.name == name }
    }
}
