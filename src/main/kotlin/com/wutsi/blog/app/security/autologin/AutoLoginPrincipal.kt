package com.wutsi.blog.app.security.autologin

import java.security.Principal


class AutoLoginPrincipal(
        val accessToken: String
) : Principal {
    override fun getName() = accessToken
}
