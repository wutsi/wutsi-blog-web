package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.page.blog.model.NextBestActionModel
import com.wutsi.blog.app.page.settings.model.UserModel

interface NextAction {
    fun get(blog: UserModel): NextBestActionModel?
}
