package com.wutsi.blog.app.component.announcement

import com.wutsi.blog.app.component.announcement.service.AnnouncementService
import com.wutsi.blog.app.component.comment.service.CommentAnnouncement
import com.wutsi.blog.app.component.like.service.LikeAnnouncement
import com.wutsi.blog.app.page.partner.service.WPPAnnouncement
import com.wutsi.blog.app.page.settings.service.SocialLinksAnnouncement
import com.wutsi.core.logging.KVLogger
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
    private lateinit var like: LikeAnnouncement

    @Autowired
    private lateinit var comment: CommentAnnouncement

    @Autowired
    private lateinit var logger: KVLogger

    @Bean
    fun announcementService() = AnnouncementService(
        announcements = arrayListOf(
//                    socialLinks,
            like,
            comment,
            wpp
        ),
        logger = logger
    )
}
