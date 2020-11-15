package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.backend.SortBackend
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.editor.model.PublishForm
import com.wutsi.blog.app.page.editor.model.ReadabilityModel
import com.wutsi.blog.app.page.editor.service.EJSFilterSet
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryForm
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.home.model.WallModel
import com.wutsi.blog.client.story.ImportStoryRequest
import com.wutsi.blog.client.story.PublishStoryRequest
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.client.story.SortStoryResponse
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.StorySummaryDto
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import java.io.StringWriter

@Service
class StoryService(
        private val requestContext: RequestContext,
        private val mapper: StoryMapper,
        private val storyBackend: StoryBackend,
        private val sortBackend: SortBackend,
        private val ejsJsonReader: EJSJsonReader,
        private val ejsHtmlWriter: EJSHtmlWriter,
        private val ejsFilters: EJSFilterSet,
        private val userService: UserService
) {
    fun save(editor: StoryForm): StoryForm {
        var response = SaveStoryResponse()
        val request = toSaveStoryRequest(editor)
        if (shouldCreate(editor)){
            response = storyBackend.create(request)
        } else if (shouldUpdate(editor)) {
            response = storyBackend.update(editor.id!!, request)
        }

        return StoryForm(
                id = response.storyId,
                title = editor.title,
                content = editor.content
        )
    }

    fun get(id: Long): StoryModel {
        val story = storyBackend.get(id).story
        val user = userService.get(story.userId)
        return mapper.toStoryModel(story, user)
    }

    fun translate(id: Long, language: String): StoryModel {
        val story = storyBackend.translate(id, language).story
        val user = userService.get(story.userId)
        return mapper.toStoryModel(story, user)
    }

    fun search(request: SearchStoryRequest): List<StoryModel> {
        val stories = storyBackend.search(request).stories
        if (stories.isEmpty()){
            return emptyList()
        }

        val users = searchUserMap(stories)
        return stories.map { mapper.toStoryModel(it, users[it.userId]) }
    }

    fun sort(
            stories: List<StoryModel>,
            algorithm: SortAlgorithmType,
            statsHoursOffset: Int=24*7,
            bubbleDownViewedStories:Boolean = true
    ): List<StoryModel> {
        if (stories.size <= 1)
            return stories

        val response = doSort(stories, algorithm, statsHoursOffset, bubbleDownViewedStories)
        val storyMap = stories.map { it.id to it }.toMap()
        return response.storyIds
                .map { storyMap[it] }
                .filter { it != null }
                as List<StoryModel>
    }

    fun searchWallStories(): WallModel {
        val stories = search(SearchStoryRequest(
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                limit = 50
        ))
        if (stories.isEmpty()){
            return WallModel()
        }

        val sortResponse = doSort(
                stories = stories,
                algorithm = SortAlgorithmType.most_viewed,
                statsHoursOffset = 24*1, // 1 days
                bubbleDownViewedStories = true
        )

        val mainStory = findMainStory(stories, sortResponse)
        val featureStories = findFeaturedStories(stories, mainStory)
        return WallModel(
                mainStory = mainStory,
                stories = stories,
                featureStories = featureStories,
                popularStories = findPopularStories(stories, featureStories, mainStory)
        )
    }

    private fun findMainStory(stories: List<StoryModel>, sortResponse: SortStoryResponse): StoryModel? {
        val notViewed = stories.filter { !sortResponse.viewedStoryIds.contains(it.id) }
        var withThumbnail = notViewed.filter { it.thumbnailUrl != null && it.thumbnailUrl.isNotEmpty() }
        if (withThumbnail.isEmpty()) {
            withThumbnail = stories.filter { it.thumbnailUrl != null && it.thumbnailUrl.isNotEmpty() }
        }

        return if (withThumbnail.isEmpty()){
            null
        } else {
            val index = withThumbnail.size.toDouble() * Math.random()
            return withThumbnail[index.toInt()]
        }
    }

    private fun findFeaturedStories(
            stories: List<StoryModel>,
            main: StoryModel?
    ): List<StoryModel> {
        val result = LinkedHashMap<UserModel, StoryModel>()
        stories
                .filter{ it.id != main?.id }
                .forEach{
                    val user = it.user
                    if (!result.containsKey(user) && user.id != main?.user?.id){
                        result[user] = it
                    }
                }

        return result.values
                .toList()
                .sortedByDescending { it.publishedDateTime }
    }

    private fun findPopularStories(
            stories: List<StoryModel>,
            featuredStories: List<StoryModel>,
            main: StoryModel?
    ): List<StoryModel> {
        val result = sort(
                stories = stories,
                algorithm = SortAlgorithmType.most_viewed,
                statsHoursOffset = 24*7, // 7 days
                bubbleDownViewedStories = false
        )

        val featuredIds = featuredStories.map { it.id }
        return result
                .filter {  main?.id != it.id && !featuredIds.contains(it.id) }
                .take(5)
    }

    private fun doSort(stories: List<StoryModel>, algorithm: SortAlgorithmType, statsHoursOffset: Int, bubbleDownViewedStories:Boolean = true): SortStoryResponse {
        return sortBackend.sort(SortStoryRequest(
                storyIds = stories.map { it.id },
                bubbleDownViewedStories =  bubbleDownViewedStories,
                userId = requestContext.currentUser()?.id,
                deviceId = requestContext.deviceId(),
                algorithm = algorithm,
                statsHoursOffset = statsHoursOffset
        ))
    }


    fun publish(editor: PublishForm){
        storyBackend.publish(editor.id, PublishStoryRequest(
                title = editor.title,
                tagline = editor.tagline,
                summary = editor.summary,
                topidId = editor.topicId.toLong(),
                tags = editor.tags
        ))
    }

    fun count(status: StoryStatus): Int {
        val userId = requestContext.currentUser()?.id
        val request = SearchStoryRequest(
                userIds = if (userId == null) emptyList() else listOf(userId),
                status = status,
                limit = Int.MAX_VALUE
        )
        return storyBackend.count(request).total
    }

    fun import(url: String): Long {
        val request = ImportStoryRequest(
                url = url,
                accessToken = requestContext.accessToken()
        )
        return storyBackend.import(request).storyId
    }

    fun readability(id: Long): ReadabilityModel {
        val result = storyBackend.readability(id).readability
        return mapper.toReadabilityModel(result)
    }

    fun delete(id: Long) {
        storyBackend.delete(id)
    }

    private fun shouldUpdate(editor: StoryForm) =  editor.id != null && editor.id > 0L

    private fun shouldCreate(editor: StoryForm) = (editor.id == null || editor.id == 0L) && !isEmpty(editor)

    private fun isEmpty(editor: StoryForm): Boolean {
        if (editor.title.trim().isNotEmpty()){
            return false
        }

        val doc = ejsJsonReader.read(editor.content)
        val html = StringWriter()
        ejsHtmlWriter.write(doc, html)
        return Jsoup.parse(html.toString()).body().text().trim().isEmpty()
    }

    private fun toSaveStoryRequest(editor: StoryForm) = SaveStoryRequest(
            contentType = "application/editorjs",
            content = editor.content,
            title = editor.title,
            accessToken = requestContext.accessToken()
    )


    private fun searchUserMap(stories: List<StorySummaryDto>): Map<Long, UserModel?> {
        val userIds = stories.map { it.userId }.toSet().toList()
        return userService.search(SearchUserRequest(
                userIds = userIds,
                limit = userIds.size,
                offset = 0
        ))
                .map { it.id to it }
                .toMap()
    }

    fun generateHtmlContent(story: StoryModel): String {
        if (story.content == null){
            return ""
        }

        val ejs = ejsJsonReader.read(story.content)
        val html = StringWriter()
        ejsHtmlWriter.write(ejs, html)

        val doc = Jsoup.parse(html.toString())
        ejsFilters.filter(doc)
        return doc.html()
    }
}

