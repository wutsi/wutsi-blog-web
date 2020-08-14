package com.wutsi.blog.app.page.comment.service

import com.wutsi.blog.app.common.service.Moment
import com.wutsi.blog.app.page.comment.model.CommentCountModel
import com.wutsi.blog.app.page.comment.model.CommentModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.client.comment.CommentCountDto
import com.wutsi.blog.client.comment.CommentDto
import com.wutsi.core.util.NumberUtils
import org.springframework.stereotype.Service

@Service
class CommentMapper(private val moment: Moment){
    fun toCommentModel(obj: CommentDto, users: Map<Long, UserModel>) = CommentModel(
            id = obj.id,
            text = obj.text,
            modificationDateTime = moment.format(obj.modificationDateTime),
            user = users[obj.userId]
    )

    fun toCommentCountModel(obj: CommentCountDto) = CommentCountModel(
            storyId = obj.storyId,
            value = obj.value,
            text = NumberUtils.toHumanReadable(obj.value)
    )
}
