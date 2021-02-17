package com.wutsi.blog.app.page.settings.service

import com.wutsi.blog.app.common.service.ImageKitService
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.client.user.UserDto
import com.wutsi.blog.client.user.UserSummaryDto
import com.wutsi.core.util.NumberUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class UserMapper(
    private val imageKit: ImageKitService,
    @Value("\${wutsi.image.user.small.width}") private val pictureSmallWidth: Int
) {
    fun toUserModel(user: UserDto) = UserModel(
        id = user.id,
        name = user.name,
        biography = user.biography,
        fullName = user.fullName,
        pictureUrl = user.pictureUrl,
        pictureSmallUrl = imageKit.transform(user.pictureUrl, pictureSmallWidth.toString()),
        websiteUrl = user.websiteUrl,
        email = user.email,
        loginCount = user.loginCount,
        slug = slug(user),
        facebookUrl = user.facebookId?.let { "https://www.facebook.com/$it" } ?: null,
        linkedinUrl = user.linkedinId?.let { "https://www.linkedin.com/in/$it" } ?: null,
        twitterUrl = user.twitterId?.let { "https://www.twitter.com/$it" } ?: null,
        youtubeUrl = user.youtubeId?.let { "https://www.youtube.com/channel/$it" } ?: null,
        telegramUrl = user.telegramId?.let { "http://t.me/$it" } ?: null,
        whatsappUrl = user.whatsappId?.let { "http://wa.me/$it" } ?: null,
        messengerUrl = user.facebookId?.let { "http://m.me/$it" } ?: null,
        rssUrl = slug(user) + "/rss",
        superUser = user.superUser,
        blog = user.blog,
        storyCount = user.storyCount,
        followerCount = user.followerCount,
        followerCountText = NumberUtils.toHumanReadable(user.followerCount),
        hasFollowers = user.followerCount > 0,
        readAllLanguages = user.readAllLanguages,
        language = user.language,
        locale = if (user.language == null) null else Locale(user.language, "CM"),
        facebookId = user.facebookId,
        twitterId = user.twitterId,
        linkedinId = user.linkedinId,
        youtubeId = user.youtubeId,
        telegramId = user.telegramId,
        whatsappId = user.whatsappId,
        hasInstantMessagingLinks = !user.telegramId.isNullOrEmpty() ||
            !user.whatsappId.isNullOrEmpty(),
        hasSocialLinks = !user.facebookId.isNullOrEmpty() ||
            !user.youtubeId.isNullOrEmpty() ||
            !user.linkedinId.isNullOrEmpty() ||
            !user.twitterId.isNullOrEmpty()

    )

    fun slug(user: UserDto) = "/@/${user.name}"

    fun slug(user: UserSummaryDto) = "/@/${user.name}"

    fun toUserModel(user: UserSummaryDto) = UserModel(
        id = user.id,
        name = user.name,
        fullName = user.fullName,
        pictureUrl = user.pictureUrl,
        pictureSmallUrl = imageKit.transform(user.pictureUrl, pictureSmallWidth.toString()),
        slug = slug(user),
        biography = user.biography,
        storyCount = user.storyCount,
        followerCount = user.followerCount,
        hasFollowers = user.followerCount > 0,
        followerCountText = NumberUtils.toHumanReadable(user.followerCount)
    )
}
