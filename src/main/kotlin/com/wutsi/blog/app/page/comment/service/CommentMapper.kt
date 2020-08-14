package com.wutsi.blog.app.page.comment.service

import com.wutsi.blog.app.common.service.Moment
import com.wutsi.blog.app.page.comment.model.CommentModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.client.comment.CommentDto
import org.springframework.stereotype.Service

@Service
class CommentMapper(private val moment: Moment){
    fun toCommentModel(obj: CommentDto, users: Map<Long, UserModel>) = CommentModel(
            id = obj.id,
            text = obj.text,
            modificationDateTime = moment.format(obj.modificationDateTime),
            user = users[obj.userId]
    )
}
