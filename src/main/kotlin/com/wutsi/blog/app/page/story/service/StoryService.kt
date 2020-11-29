package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.editor.model.PublishForm
import com.wutsi.blog.app.page.editor.model.ReadabilityModel
import com.wutsi.blog.app.page.editor.service.EJSFilterSet
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryForm
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.client.story.ImportStoryRequest
import com.wutsi.blog.client.story.PublishStoryRequest
import com.wutsi.blog.client.story.RecommendStoryRequest
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
import java.io.StringWriter

@Service
class StoryService(
        private val requestContext: RequestContext,
        private val mapper: StoryMapper,
        private val backend: StoryBackend,
        private val ejsJsonReader: EJSJsonReader,
        private val ejsHtmlWriter: EJSHtmlWriter,
        private val ejsFilters: EJSFilterSet,
        private val userService: UserService
) {
    fun save(editor: StoryForm): StoryForm {
        var response = SaveStoryResponse()
        val request = toSaveStoryRequest(editor)
        if (shouldCreate(editor)){
            response = backend.create(request)
        } else if (shouldUpdate(editor)) {
            response = backend.update(editor.id!!, request)
        }

        return StoryForm(
                id = response.storyId,
                title = editor.title,
                content = editor.content
        )
    }

    fun get(id: Long): StoryModel {
        val story = backend.get(id).story
        val user = userService.get(story.userId)
        return mapper.toStoryModel(story, user)
    }

    fun translate(id: Long, language: String): StoryModel {
        val story = backend.translate(id, language).story
        val user = userService.get(story.userId)
        return mapper.toStoryModel(story, user)
    }

    fun search(request: SearchStoryRequest): List<StoryModel> {
        val stories = backend.search(request).stories
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

    private fun doSort(stories: List<StoryModel>, algorithm: SortAlgorithmType, statsHoursOffset: Int, bubbleDownViewedStories:Boolean = true): SortStoryResponse {
        return backend.sort(SortStoryRequest(
                storyIds = stories.map { it.id },
                bubbleDownViewedStories =  bubbleDownViewedStories,
                userId = requestContext.currentUser()?.id,
                deviceId = requestContext.deviceId(),
                algorithm = algorithm,
                statsHoursOffset = statsHoursOffset
        ))
    }


    fun publish(editor: PublishForm){
        backend.publish(editor.id, PublishStoryRequest(
                title = editor.title,
                tagline = editor.tagline,
                summary = editor.summary,
                topidId = editor.topicId.toLong(),
                tags = editor.tags,
                socialMediaMessage = editor.socialMediaMessage
        ))
    }

    fun count(status: StoryStatus? = null): Int {
        val userId = requestContext.currentUser()?.id
        val request = SearchStoryRequest(
                userIds = if (userId == null) emptyList() else listOf(userId),
                status = status,
                limit = Int.MAX_VALUE
        )
        return backend.count(request).total
    }

    fun import(url: String): Long {
        val request = ImportStoryRequest(
                url = url,
                accessToken = requestContext.accessToken()
        )
        return backend.import(request).storyId
    }

    fun readability(id: Long): ReadabilityModel {
        val result = backend.readability(id).readability
        return mapper.toReadabilityModel(result)
    }

    fun delete(id: Long) {
        backend.delete(id)
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

    fun recommend(storyId: Long): List<StoryModel> {
        val response = backend.recommend(RecommendStoryRequest(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = requestContext.deviceId(),
                limit = 9
        ))

        return if (response.storyIds.isEmpty()) emptyList() else search(SearchStoryRequest(
                storyIds = response.storyIds,
                sortBy = StorySortStrategy.no_sort
        ))
    }

}

