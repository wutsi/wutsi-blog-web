package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.AccountModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.client.user.UserDto
import com.wutsi.blog.client.user.UserSummaryDto
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toUserModel(user: UserDto) = UserModel(
            id = user.id,
            name = user.name,
            biography = user.biography,
            fullName = user.fullName,
            pictureUrl = user.pictureUrl,
            websiteUrl = user.websiteUrl,
            email = user.email,
            loginCount = user.loginCount,
            slug = "/@/${user.name}",
            facebookUrl = facebookUrl(user),
            linkedInUrl = linkedIn(user),
            twitterUrl = twitter(user),
            accounts = user.accounts.map { AccountModel(
                    id = it.id,
                    provider = it.provider,
                    providerUserId = it.providerUserId,
                    loginCount = it.loginCount
            ) }
    )

    private fun facebookUrl(user: UserDto): String? {
        val account = user.accounts.find { it.provider == "facebook" }
        return if (account == null) null else "https://www.facebook.com/${account.providerUserId}"
    }

    private fun twitter(user: UserDto): String? {
        val account = user.accounts.find { it.provider == "twitter" }
        return if (account == null) null else "https://www.twitter.com/${account.providerUserId}"
    }

    private fun linkedIn(user: UserDto): String? {
        val account = user.accounts.find { it.provider == "linkedin" }
        return if (account == null) null else "https://www.linkedin.com/in/${account.providerUserId}"
    }

    fun toUserModel(user: UserSummaryDto) = UserModel(
            id = user.id,
            name = user.name,
            fullName = user.fullName,
            pictureUrl = user.pictureUrl,
            slug = "/@/${user.name}"
    )
}
