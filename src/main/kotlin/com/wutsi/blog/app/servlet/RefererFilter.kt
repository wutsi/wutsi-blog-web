package com.wutsi.blog.app.servlet

import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import com.wutsi.core.logging.KVLogger
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class RefererFilter(
        private val logger: KVLogger,
        private val domain: String
) : Filter {
    companion object {
        const val DIRECT = "REDIRECT"
        const val LOGGER_KEY = "HttpRequestReferer"
    }
    override fun destroy() {
    }

    override fun init(config: FilterConfig?) {
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            val referer = getReferer(request)
            storeReferer(referer, request, response)
        } finally {
            chain.doFilter(request, response)
        }
    }

    private fun getReferer(req: ServletRequest): String {
        val referer = (req as HttpServletRequest).getHeader("Referer")
        return if (referer == null || referer.isEmpty()) DIRECT else referer
    }

    private fun storeReferer(referer: String, request: ServletRequest, response: ServletResponse) {
        logger.add(LOGGER_KEY, referer)
        if (isExternalTraffic(referer)) {
            CookieHelper.put(CookieName.REFERER, referer, request as HttpServletRequest, response as HttpServletResponse)
        }
    }

    private fun isExternalTraffic(referer: String) = referer.isEmpty() || referer.indexOf(domain) < 0
}
