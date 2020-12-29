package com.wutsi.blog.app.page.calendar.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.model.CalendarPostModel
import com.wutsi.blog.app.page.calendar.model.CreatePostForm
import com.wutsi.blog.app.page.calendar.model.UpdatePostForm
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.post.CreatePostRequest
import com.wutsi.blog.client.post.SearchPostRequest
import com.wutsi.blog.client.post.SetPostPictureRequest
import com.wutsi.blog.client.post.UpdatePostRequest
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.sdk.PostApi
import com.wutsi.core.util.DateUtils
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.LocalDate

@Service
class PostService(
    private val api: PostApi,
    private val mapper: CalendarMapper,
    private val storyService: StoryService,
    private val channelService: ChannelService,
    private val requestContext: RequestContext
) {
    fun get(id: Long): CalendarPostModel {
        val post = api.get(id).post
        val story = storyService.get(post.storyId)
        val channel = channelService.all().find { it.type == post.channelType }
        return mapper.toPostModel(post, channel, story)
    }

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

        // Channels
        val channelMap: Map<ChannelType, ChannelModel> = channelService.all().map { it.type to it }.toMap()

        return posts.map {
            mapper.toPostModel(it, channelMap[it.channelType], storyMap[it.storyId])
        }
    }

    fun create(form: CreatePostForm): Long {
        return api.create(
            CreatePostRequest(
                storyId = form.storyId.toLong(),
                channelType = form.channelType,
                scheduledPostDateTime = SimpleDateFormat("yyyy-MM-dd").parse(form.scheduledDateTime),
                message = form.message,
                includeLink = form.includeLink
            )
        ).postId
    }

    fun update(form: UpdatePostForm) {
        api.update(
            form.id,
            UpdatePostRequest(
                message = form.message,
                scheduledPostDateTime = SimpleDateFormat("yyyy-MM-dd").parse(form.scheduledDateTime),
                includeLink = form.includeLink
            )
        )
    }

    fun delete(id: Long) {
        api.delete(id)
    }

    fun setPicture(id: Long, pictureUrl: String) {
        api.setPicture(
            postId = id,
            request = SetPostPictureRequest(
                pictureUrl = pictureUrl
            )
        )
    }
}
