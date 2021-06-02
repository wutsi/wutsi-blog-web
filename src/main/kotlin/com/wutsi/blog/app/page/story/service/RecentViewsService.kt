package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import com.wutsi.stats.StatsApi
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RecentViewsService(
    private val requestContext: RequestContext,
    private val statsApi: StatsApi
) {
    fun add(storyId: Long) {
        val viewed = getViewedFromCookie()
        if (!viewed.contains(storyId)) {
            viewed.add(storyId)
            val value = viewed.toSet().joinToString(",")
            CookieHelper.put(CookieName.VIEWS, value, requestContext.request, requestContext.response)
        }
    }

    fun get(): List<Long> {
        val ids = getViewedFromCookie()
        ids.addAll(getViewedFromServer())

        return ids.toSet().toList()
    }

    private fun getViewedFromCookie(): MutableList<Long> {
        val viewed = CookieHelper.get(CookieName.VIEWS, requestContext.request)

        return if (viewed.isNullOrEmpty())
            return mutableListOf()
        else
            viewed.split(",")
                .toList()
                .map { it.trim() }
                .map { it.toLong() }
                .toMutableList()
    }

    private fun getViewedFromServer(): List<Long> {
        val today = LocalDate.now()
        return try {
            statsApi.views(
                userId = requestContext.currentUser()?.id,
                deviceId = requestContext.deviceId(),
                startDate = today.minusDays(7),
                endDate = today,
                limit = 100
            ).views.map { it.storyId }
        } catch (ex: Exception) {
            emptyList()
        }
    }
}
