package com.wutsi.blog.app.page.calendar.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.model.CalendarPostModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.client.post.SearchPostRequest
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.sdk.PostApi
import com.wutsi.core.util.DateUtils
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class PostService(
    private val api: PostApi,
    private val mapper: CalendarMapper,
    private val storyService: StoryService,
    private val requestContext: RequestContext
) {
    fun search(startDate: LocalDate, endDate: LocalDate): List<CalendarPostModel> {
        // Posts
        val posts = api.search(
            SearchPostRequest(
                userId = requestContext.currentUser()?.id,
                scheduledPostStartDateTime = DateUtils.toDate(startDate),
                scheduledPostEndDateTime = DateUtils.toDate(endDate),
                limit = 100
            )
        ).posts

        // Stories
        val storyIds = posts.map { it.storyId }
        val storyMap: Map<Long, StoryModel> = if (storyIds.isEmpty()) {
            emptyMap()
        } else {
            storyService.search(SearchStoryRequest(storyIds = storyIds))
                .map { it.id to it }
                .toMap()
        }

        return posts.map {
            mapper.toPostModel(it, storyMap[it.storyId])
        }
    }
}
