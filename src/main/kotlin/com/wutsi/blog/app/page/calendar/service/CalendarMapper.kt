package com.wutsi.blog.app.page.calendar.service

import com.wutsi.blog.app.common.service.LocalizationService
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.model.CalendarPostModel
import com.wutsi.blog.app.page.calendar.model.CalendarStoryModel
import com.wutsi.blog.app.page.calendar.model.DayOfWeekModel
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.client.post.PostDto
import com.wutsi.blog.client.post.PostSummaryDto
import com.wutsi.blog.client.story.StoryStatus.draft
import com.wutsi.blog.client.story.StoryStatus.published
import com.wutsi.core.util.DateUtils
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Service
class CalendarMapper(
    private val requestContext: RequestContext,
    private val localizationService: LocalizationService
) {
    fun toPostModel(post: PostDto, channel: ChannelModel?, story: StoryModel?) = CalendarPostModel(
        id = post.id,
        status = post.status,
        story = story?.let { toCalendarStoryModel(it) } ?: CalendarStoryModel(),
        pictureUrl = post.pictureUrl,
        message = post.message,
        channel = channel?.let { it } ?: ChannelModel(),
        channelType = post.channelType,
        scheduledPostDateTime = post.scheduledPostDateTime,
        channelImageUrl = "/assets/wutsi/img/social/${post.channelType.name}.png"
    )

    fun toPostModel(post: PostSummaryDto, channel: ChannelModel?, story: StoryModel?) = CalendarPostModel(
        id = post.id,
        status = post.status,
        story = story?.let { toCalendarStoryModel(it) } ?: CalendarStoryModel(),
        pictureUrl = post.pictureUrl,
        message = post.message,
        channel = channel?.let { it } ?: ChannelModel(),
        channelType = post.channelType,
        scheduledPostDateTime = post.scheduledPostDateTime,
        channelImageUrl = "/assets/wutsi/img/social/${post.channelType.name}.png"
    )

    fun toCalendarStoryModel(story: StoryModel) = CalendarStoryModel(
        id = story.id,
        title = story.title,
        status = story.status,
        scheduledPublishDateTime = story.scheduledPublishDateTimeAsDate,
        publishDateTime = story.publishedDateTimeAsDate
    )

    fun toDayOfWeekModel(
        date: LocalDate,
        posts: List<CalendarPostModel>,
        stories: List<CalendarStoryModel>,
        user: UserModel?
    ) = DayOfWeekModel(
        name = requestContext.getMessage("label.day_of_week.${date.dayOfWeek.value}"),
        date = date,
        displayDate = date.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(localizationService.getLocale())
        ),
        newsletterDelivery = date.dayOfWeek.value == user?.newsletterDeliveryDayOfWeek,
        posts = posts.filter { date == DateUtils.toLocalDate(it.scheduledPostDateTime) },
        storiesToPublish = stories.filter { publishedOn(date, it) }
    )

    private fun publishedOn(date: LocalDate, story: CalendarStoryModel): Boolean {
        if (story.status == published) {
            return date == DateUtils.toLocalDate(story.publishDateTime!!)
        } else if (story.status == draft && story.scheduledPublishDateTime != null) {
            return date == DateUtils.toLocalDate(story.scheduledPublishDateTime)
        }
        return false
    }
}
