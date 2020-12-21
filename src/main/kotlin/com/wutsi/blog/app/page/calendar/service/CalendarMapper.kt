package com.wutsi.blog.app.page.calendar.service

import com.wutsi.blog.app.common.service.LocalizationService
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.model.CalendarPostModel
import com.wutsi.blog.app.page.calendar.model.CalendarStoryModel
import com.wutsi.blog.app.page.calendar.model.DayOfWeekModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.client.post.PostSummaryDto
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
    fun toPostModel(post: PostSummaryDto, story: StoryModel?) = CalendarPostModel(
        id = post.id,
        status = post.status,
        story = story?.let { toCalendarStoryModel(it) } ?: CalendarStoryModel(),
        pictureUrl = post.pictureUrl,
        message = post.message,
        channelType = post.channelType,
        scheduledPostDateTime = post.scheduledPostDateTime,
        channelImageUrl = "/assets/wutsi/img/social/${post.channelType.name}.png"
    )

    fun toCalendarStoryModel(story: StoryModel) = CalendarStoryModel(
        id = story.id,
        title = story.title,
        status = story.status,
        scheduledPublishDateTime = story.scheduledPublishDateTimeAsDate
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
        storiesToPublish = stories.filter {
            it.scheduledPublishDateTime != null &&
                DateUtils.toLocalDate(it.scheduledPublishDateTime).dayOfWeek.value == date.dayOfWeek.value
        }
    )
}
