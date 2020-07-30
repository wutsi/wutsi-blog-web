package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.common.service.Moment
import com.wutsi.blog.app.page.editor.model.ReadabilityModel
import com.wutsi.blog.app.page.editor.model.ReadabilityRuleModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.model.TopicModel
import com.wutsi.blog.client.story.ReadabilityDto
import com.wutsi.blog.client.story.StoryDto
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.StorySummaryDto
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class StoryMapper(
        private val tagMapper: TagMapper,
        private val topicMapper: TopicMapper,
        private val topicService: TopicService,
        private val moment: Moment,
        private val htmlImageMapper: HtmlImageModelMapper
) {
    companion object {
        const val MAX_TAGS: Int = 5
    }

    fun toStoryModel(story: StoryDto, user: UserModel? = null): StoryModel {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm.ss.SSSZ")
        return StoryModel(
                id = story.id,
                content = story.content,
                title = nullToEmpty(story.title),
                tagline = nullToEmpty(story.tagline),
                contentType = story.contentType,
                thumbnailUrl = if (story.thumbnailUrl == null) null else story.thumbnailUrl,
                thumbnailImage = htmlImageMapper.toHtmlImageMapper(story.thumbnailUrl),
                wordCount = story.wordCount,
                sourceUrl = story.sourceUrl,
                sourceSite = story.sourceSite,
                readingMinutes = story.readingMinutes,
                language = story.language,
                summary = nullToEmpty(story.summary),
                user = if (user == null) UserModel(id = story.userId) else user,
                status = story.status,
                draft = story.status == StoryStatus.draft,
                published = story.status == StoryStatus.published,
                modificationDateTime = moment.format(story.modificationDateTime),
                creationDateTime = moment.format(story.creationDateTime),
                publishedDateTimeAsDate = story.publishedDateTime,
                publishedDateTime = moment.format(story.publishedDateTime),
                creationDateTimeISO8601 = fmt.format(story.creationDateTime),
                publishedDateTimeISO8601 = if (story.publishedDateTime == null) null else fmt.format(story.publishedDateTime),
                modificationDateTimeISO8601 = fmt.format(story.modificationDateTime),
                readabilityScore = story.readabilityScore,
                slug = story.slug,
                tags = story.tags
                        .sortedByDescending { it.totalStories }
                        .take(MAX_TAGS)
                        .map { tagMapper.toTagModel(it) },
                topic = if (story.topic == null) TopicModel() else topicMapper.toTopicMmodel(story.topic!!),
                liveDateTime = moment.format(story.liveDateTime),
                live = story.live,
                wppStatus = story.wppStatus
        )
    }

    fun toStoryModel(story: StorySummaryDto, user: UserModel? = null) = StoryModel(
            id = story.id,
            title = nullToEmpty(story.title),
            tagline = nullToEmpty(story.tagline),
            thumbnailUrl = story.thumbnailUrl,
            thumbnailImage = htmlImageMapper.toHtmlImageMapper(story.thumbnailUrl),
            wordCount = story.wordCount,
            sourceUrl = story.sourceUrl,
            readingMinutes = story.readingMinutes,
            language = story.language,
            summary = nullToEmpty(story.summary),
            user = if (user == null) UserModel(id = story.userId) else user,
            status = story.status,
            draft = story.status == StoryStatus.draft,
            published = story.status == StoryStatus.published,
            modificationDateTime = moment.format(story.modificationDateTime),
            creationDateTime = moment.format(story.creationDateTime),
            publishedDateTime = moment.format(story.publishedDateTime),
            publishedDateTimeAsDate = story.publishedDateTime,
            slug = story.slug,
            topic = if (story.topicId == null) TopicModel() else nullToEmpty(topicService.get(story.topicId!!)),
            liveDateTime = moment.format(story.liveDateTime),
            live = story.live,
            wppStatus = story.wppStatus
    )

    fun toReadabilityModel(obj: ReadabilityDto) = ReadabilityModel(
            score = obj.score,
            scoreThreshold = obj.scoreThreshold,
            color = readabilityColor(obj.score),
            rules = obj.rules.map {
                ReadabilityRuleModel(
                        name = it.name,
                        score = it.score,
                        color = readabilityColor(it.score)
                )
            }
    )

    private fun nullToEmpty(value: String?): String {
        return if (value == null) "" else value
    }

    private fun nullToEmpty(topic: TopicModel?): TopicModel {
        return if (topic == null) TopicModel() else topic
    }

    private fun readabilityColor(score: Int): String {
        if (score <= 50) {
            return "red"
        } else if (score <= 75) {
            return "yellow"
        } else {
            return "green"
        }
    }
}
