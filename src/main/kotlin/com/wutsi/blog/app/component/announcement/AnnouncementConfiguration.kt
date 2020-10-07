package com.wutsi.blog.app.component.announcement

import com.wutsi.blog.app.component.announcement.service.AnnouncementService
import com.wutsi.blog.app.component.comment.service.CommentAnnouncement
import com.wutsi.blog.app.page.settings.service.SocialLinksAnnouncement
import com.wutsi.blog.app.page.channel.service.TwitterAnnouncement
import com.wutsi.blog.app.page.partner.service.WPPAnnouncement
import com.wutsi.blog.app.component.like.service.LikeAnnouncement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AnnouncementConfiguration {
    @Autowired
    private lateinit var wpp: WPPAnnouncement

    @Autowired
    private lateinit var socialLinks: SocialLinksAnnouncement

    @Autowired
    private lateinit var twitter: TwitterAnnouncement

    @Autowired
    private lateinit var like: LikeAnnouncement

    @Autowired
    private lateinit var comment: CommentAnnouncement

    @Bean
    fun announcementService() = AnnouncementService(
            announcements = arrayListOf(
                    like,
                    comment,
                    socialLinks,
                    wpp,
                    twitter
            ))
}
