package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.editor.StoryEditor
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service

@Service
class StoryService(
        private val storyBackend: StoryBackend,
        private val oauth2ClientService: OAuth2AuthorizedClientService
) {
    fun save(editor: StoryEditor): StoryEditor {
        val response: SaveStoryResponse
        val request = toSaveStoryRequest(editor)
        if (shouldCreate(editor)){
            response = storyBackend.create(request)
        } else {
            response = storyBackend.update(editor.id, request)
        }

        return StoryEditor(
                id = response.storyId,
                title = editor.title,
                content = editor.content
        )
    }

    fun get(id: Long): StoryModel {

    }

    protected fun accessToken(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is AnonymousAuthenticationToken) {
            return null
        }

        val ooauth2Token = auth as OAuth2AuthenticationToken
        val client = oauth2ClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(ooauth2Token.authorizedClientRegistrationId, auth.name)
        return client.accessToken.tokenValue
    }

    private fun shouldCreate(editor: StoryEditor) = (editor.id == 0L)

    private fun toSaveStoryRequest(editor: StoryEditor) = SaveStoryRequest(
            contentType = "application/editorjs",
            content = editor.content,
            title = editor.title,
            accessToken = accessToken()
    )
}
