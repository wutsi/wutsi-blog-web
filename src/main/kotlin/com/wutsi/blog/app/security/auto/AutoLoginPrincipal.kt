package com.wutsi.blog.app.security.auto

import java.security.Principal


class AutoLoginPrincipal(
        val accessToken: String
) : Principal {
    override fun getName() = accessToken
}
