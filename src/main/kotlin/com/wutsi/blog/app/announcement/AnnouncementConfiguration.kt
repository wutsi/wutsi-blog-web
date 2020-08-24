package com.wutsi.blog.app.announcement

import com.wutsi.blog.app.announcement.service.AnnouncementService
import com.wutsi.blog.app.announcement.service.impl.SocialLinksAnnouncement
import com.wutsi.blog.app.announcement.service.impl.TwitterAnnouncement
import com.wutsi.blog.app.announcement.service.impl.WPPAnnouncement
import com.wutsi.blog.app.common.service.RequestContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("aws")
class AnnouncementConfiguration {

    @Autowired
    private lateinit var requestContext: RequestContext

    @Autowired
    private lateinit var wpp: WPPAnnouncement

    @Autowired
    private lateinit var socialLinks: SocialLinksAnnouncement

    @Autowired
    private lateinit var twitter: TwitterAnnouncement

    @Bean
    fun announcementService() = AnnouncementService(
            requestContext = requestContext,
            announcements = arrayListOf(
                    socialLinks,
                    wpp,
                    twitter
            ))
}
