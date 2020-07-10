package com.wutsi.blog.app.page.sitemap.service

import com.wutsi.blog.app.page.settings.service.UserMapper
import com.wutsi.blog.app.page.sitemap.model.UrlModel
import com.wutsi.blog.client.story.StorySummaryDto
import com.wutsi.blog.client.user.UserSummaryDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date

@Service
class SitemapMapper(
        private val userMapper: UserMapper,
        @Value("\${wutsi.base-url}") private val baseUrl: String
) {
    fun toUrlModel(path: String) = UrlModel(
            loc = "${baseUrl}${path}",
            lastmod = SimpleDateFormat("yyyy-MM-dd").format(Date())
    )

    fun toUrlModel(story: StorySummaryDto) = UrlModel(
            loc = "${baseUrl}${story.slug}",
            lastmod = SimpleDateFormat("yyyy-MM-dd").format(story.modificationDateTime)
    )

    fun toUrlModel(user: UserSummaryDto) = UrlModel(
            loc = baseUrl + userMapper.slug(user),
            lastmod = SimpleDateFormat("yyyy-MM-dd").format(Date())
    )
}

