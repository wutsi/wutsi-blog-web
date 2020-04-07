package com.wutsi.blog.app.service

import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class AccessTokenStorage {
    fun get(request: HttpServletRequest): String? = CookieHelper.get(CookieName.ACCESS_TOKEN, request)

    fun put(accessToken: String, request: HttpServletRequest, response: HttpServletResponse) {
        CookieHelper.put(CookieName.ACCESS_TOKEN, accessToken, request, response)
    }

    fun delete(response: HttpServletResponse) {
        CookieHelper.remove(CookieName.ACCESS_TOKEN, response)
    }
}
