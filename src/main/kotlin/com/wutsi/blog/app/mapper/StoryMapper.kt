package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.client.story.StoryDto
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.StorySummaryDto
import org.springframework.stereotype.Service

@Service
class StoryMapper(
        private val tagMapper: TagMapper,
        private val moment: Moment
) {
    fun toStoryModel(story: StoryDto, user: UserModel? = null) = StoryModel(
            id = story.id,
            content = story.content,
            title = story.title,
            contentType = story.contentType,
            thumbmailUrl = story.thumbnailUrl,
            wordCount = story.wordCount,
            sourceUrl = story.sourceUrl,
            readingMinutes = story.readingMinutes,
            language = story.language,
            summary = story.summary,
            user = if (user == null) UserModel(id = story.userId) else user,
            status = story.status,
            draft = story.status == StoryStatus.draft,
            published = story.status == StoryStatus.published,
            modificationDateTime = moment.format(story.modificationDateTime),
            creationDateTime = moment.format(story.creationDateTime),
            publishedDateTime = moment.format(story.publishedDateTime),
            tags = story.tags.map { tagMapper.toTagModel(it) },
            slug = story.slug
    )

    fun toStoryModel(story: StorySummaryDto, user: UserModel? = null) = StoryModel(
            id = story.id,
            title = story.title,
            thumbmailUrl = story.thumbnailUrl,
            wordCount = story.wordCount,
            sourceUrl = story.sourceUrl,
            readingMinutes = story.readingMinutes,
            language = story.language,
            summary = story.summary,
            user = if (user == null) UserModel(id = story.userId) else user,
            status = story.status,
            draft = story.status == StoryStatus.draft,
            published = story.status == StoryStatus.published,
            modificationDateTime = moment.format(story.modificationDateTime),
            creationDateTime = moment.format(story.creationDateTime),
            publishedDateTime = moment.format(story.publishedDateTime),
            slug = story.slug
    )
}
