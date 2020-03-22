package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.SessionModel
import com.wutsi.blog.client.user.SessionDto
import org.springframework.stereotype.Service

@Service
class SessionMapper(private val mapper: UserMapper) {
    fun toSessionModel(session: SessionDto) = SessionModel(
            accessToken = session.accessToken,
            refreshToken = session.refreshToken,
            logoutDateTime = session.logoutDateTime,
            loginDateTime = session.loginDateTime,
            accountId = session.accountId,
            user = mapper.toUserModel(session.user)
    )

}
