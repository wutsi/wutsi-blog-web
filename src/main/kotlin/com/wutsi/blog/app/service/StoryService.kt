package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.editor.PublishEditor
import com.wutsi.blog.app.editor.StoryEditor
import com.wutsi.blog.app.mapper.StoryMapper
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.client.story.PublishStoryRequest
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
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
        private val storyBackend: StoryBackend,
        private val ejsJsonReader: EJSJsonReader,
        private val ejsHtmlWriter: EJSHtmlWriter,
        private val userService: UserService
) {
    fun save(editor: StoryEditor): StoryEditor {
        var response = SaveStoryResponse()
        val request = toSaveStoryRequest(editor)
        if (shouldCreate(editor)){
            response = storyBackend.create(request)
        } else if (shouldUpdate(editor)) {
            response = storyBackend.update(editor.id!!, request)
        }

        return StoryEditor(
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

    fun search(request: SearchStoryRequest): List<StoryModel> {
        val stories = storyBackend.search(request).stories
        if (stories.isEmpty()){
            return emptyList()
        }

        val users = searchUserMap(stories)
        return stories.map { mapper.toStoryModel(it, users[it.id]) }
    }

    fun publish(editor: PublishEditor){
        storyBackend.publish(editor.id, PublishStoryRequest(
                tags = editor.tags
        ))
    }

    fun count(status: StoryStatus): Int {
        val request = SearchStoryRequest(
                userId = requestContext.user?.id,
                status = status,
                limit = Int.MAX_VALUE
        )
        return storyBackend.count(request).total
    }

    private fun shouldUpdate(editor: StoryEditor) =  editor.id != null && editor.id > 0L

    private fun shouldCreate(editor: StoryEditor) = (editor.id == null || editor.id == 0L) && !isEmpty(editor)

    private fun isEmpty(editor: StoryEditor): Boolean {
        if (editor.title.trim().isNotEmpty()){
            return false
        }

        val doc = ejsJsonReader.read(editor.content)
        val html = StringWriter()
        ejsHtmlWriter.write(doc, html)
        return Jsoup.parse(html.toString()).body().text().trim().isEmpty()
    }

    private fun toSaveStoryRequest(editor: StoryEditor) = SaveStoryRequest(
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
}

