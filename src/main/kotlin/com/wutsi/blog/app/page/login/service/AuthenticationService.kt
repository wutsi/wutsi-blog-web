package com.wutsi.blog.app.page.login.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.user.RunAsRequest
import org.springframework.stereotype.Component

@Component
class AuthenticationService(
    private val backend: AuthenticationBackend,
    private val requestContext: RequestContext
) {
    fun runAs(userName: String) {
        backend.runAs(
            RunAsRequest(
                userName = userName,
                accessToken = requestContext.accessToken()
            )
        )
    }
}
