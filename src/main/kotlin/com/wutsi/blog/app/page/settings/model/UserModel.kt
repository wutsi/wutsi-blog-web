package com.wutsi.blog.app.page.settings.model

import java.util.Locale

data class UserModel(
    val id: Long = -1,
    val name: String = "",
    val fullName: String = "",
    val email: String? = null,
    val loginCount: Long = 0,
    val pictureUrl: String? = null,
    val websiteUrl: String? = null,
    val biography: String? = null,
    val facebookUrl: String? = null,
    val twitterUrl: String? = null,
    val linkedinUrl: String? = null,
    val youtubeUrl: String? = null,
    val slug: String = "",
    val superUser: Boolean = false,
    val language: String? = null,
    val locale: Locale? = null,
    val readAllLanguages: Boolean? = null,
    val facebookId: String? = null,
    val twitterId: String? = null,
    val linkedinId: String? = null,
    val youtubeId: String? = null,
    val hasSocialLinks: Boolean = true,
    val blog: Boolean = true,
    val storyCount: Long = 0,
    val followerCount: Long = 0,
    val followerCountText: String = "",
    val newsletterDeliveryDayOfWeek: Int = -1,
    val hasNewsletter: Boolean = false,
    val hasFollowers: Boolean = false
)
