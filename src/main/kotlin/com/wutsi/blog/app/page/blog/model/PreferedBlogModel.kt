package com.wutsi.blog.app.page.blog.model

import com.wutsi.blog.app.page.settings.model.UserModel

data class PreferedBlogModel(
        val user: UserModel = UserModel(),
        val storyCount: Int? = null
)
