package com.wutsi.blog.fixtures

import com.wutsi.blog.client.user.UserDto
import com.wutsi.blog.client.user.UserSummaryDto

object UserApiFixtures {
    val DEFAULT_BIOGRAPHY: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. http://gog.me"

    fun createUserDto(
        id: Long,
        name: String,
        fullName: String,
        pictureUrl: String = "https://avatars3.githubusercontent.com/u/39621277?v=4",
        biography: String = DEFAULT_BIOGRAPHY,
        storyCount: Long = 10,
        followerCount: Long = 1,
        blog: Boolean = true,
        loginCount: Long = 4,
        language: String = "fr",
        superUser: Boolean = false
    ) = UserDto(
        id = id,
        name = name,
        fullName = fullName,
        followerCount = followerCount,
        storyCount = storyCount,
        pictureUrl = pictureUrl,
        biography = biography,
        blog = blog,
        email = "$name@gmail.com",
        language = language,
        youtubeId = name,
        linkedinId = name,
        facebookId = name,
        twitterId = name,
        loginCount = loginCount,
        superUser = superUser,
        websiteUrl = "https://www.me.com/$name"
    )

    fun createUserSummaryDto(
        id: Long,
        name: String,
        fullName: String,
        pictureUrl: String = "https://avatars3.githubusercontent.com/u/39621277?v=4",
        biography: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. http://gog.me",
        storyCount: Long = 10,
        followerCount: Long = 1,
        blog: Boolean = true
    ) = UserSummaryDto(
        id = id,
        name = name,
        fullName = fullName,
        followerCount = followerCount,
        storyCount = storyCount,
        pictureUrl = pictureUrl,
        biography = biography,
        blog = blog
    )
}
