package com.wutsi.blog.app.service

import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import org.springframework.stereotype.Component


@Component
class SecurityManager {
    fun permissions(story: StoryModel, user: UserModel?): List<Permission> {
        val permissions = mutableListOf<Permission>()

        if (story.published && story.live) {
            permissions.add(Permission.reader)
        }
        if (user?.superUser == true || story.user.id == user?.id){
            permissions.add(Permission.editor)
        }
        return permissions
    }
}
