package com.wutsi.blog.app.security.auto

import com.wutsi.blog.app.service.AccessTokenStorage
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AutoLoginFilter(
        private val storage: AccessTokenStorage,
        private val authenticationManager: AuthenticationManager,
        private val excludePaths: RequestMatcher
) : Filter {

    override fun doFilter(req: ServletRequest, resp: ServletResponse, chain: FilterChain) {
        try {
            val context = SecurityContextHolder.getContext()
            if (context.authentication is AnonymousAuthenticationToken && !exclude(req)) {
                attemptAuthentication(req as HttpServletRequest)
            }
        } finally {
            chain.doFilter(req, resp)
        }
    }

    private fun exclude(req: ServletRequest) = excludePaths.matches(req as HttpServletRequest)

    private fun attemptAuthentication(request: HttpServletRequest) {
        val accessToken = storage.get(request)
        if (accessToken != null) {
            authenticationManager.authenticate(AutoLoginAuthentication(accessToken))
        }
    }

    override fun init(config: FilterConfig) {
    }

    override fun destroy() {
    }

}
