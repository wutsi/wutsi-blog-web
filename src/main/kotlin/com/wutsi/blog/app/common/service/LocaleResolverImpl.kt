package com.wutsi.blog.app.common.service

import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import org.springframework.web.servlet.LocaleResolver
import java.util.Locale
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class LocaleResolverImpl(
        private val requestContext: RequestContext
): LocaleResolver {
    override fun resolveLocale(request: HttpServletRequest): Locale {
        return resolveFromUser(requestContext.currentUser())
                ?: resolveFromHeader(request)
    }

    private fun resolveFromUser(user: UserModel?): Locale? =
        user?.locale

    private fun resolveFromHeader(request: HttpServletRequest): Locale =
        request.locale

    override fun setLocale(request: HttpServletRequest, response: HttpServletResponse, locale: Locale?) {

    }
}
