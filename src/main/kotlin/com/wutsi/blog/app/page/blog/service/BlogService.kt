package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.backend.ViewBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.PreferedBlogModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.view.SearchPreferredAuthorRequest
import com.wutsi.blog.client.view.SearchViewRequest
import com.wutsi.core.tracking.DeviceUIDProvider
import org.springframework.stereotype.Service
import java.util.Date
import javax.servlet.http.HttpServletRequest

@Service
class BlogService(
        private val requestContext: RequestContext,
        private val viewBackend: ViewBackend,
        private val storyService: StoryService,
        private val userService: UserService,
        private val device: DeviceUIDProvider,
        private val request: HttpServletRequest
) {
    fun preferred(): List<PreferedBlogModel> {
        val user = requestContext.currentUser()
                ?: return emptyList()

        val lastViewDateTime = getLastViewDateTime(user)
                ?: return emptyList()

        val authors = findPreferredAuthors(user)
        if (authors.isEmpty()) {
            return emptyList()
        }

        val storiesByAuthorId = findStories(authors, lastViewDateTime)
        val authorMap = authors.map { it.id to it }.toMap()
        return storiesByAuthorId.keys
                .filter { authorMap[it] != null }
                .map {
                    val author = authorMap[it]!!
                    PreferedBlogModel(
                            user = author,
                            storyCount = storiesByAuthorId[author.id]?.size
                    )
                }
                .filter { it.storyCount != null && it.storyCount > 0 }
    }

    private fun getLastViewDateTime(user: UserModel): Date? {
        val views = viewBackend.search(SearchViewRequest(
                userId = user.id,
                deviceId = device.get(request),
                limit = 1
        )).views
        return if (views.isEmpty()) null else views[0].viewDateTime
    }

    private fun findPreferredAuthors(user: UserModel): List<UserModel> {
        val authorIds = viewBackend.referedAuthors(SearchPreferredAuthorRequest(
                userId = user.id,
                deviceId = device.get(request),
                limit = 3
        )).authors.map { it.authorId }
        if (authorIds.isEmpty()) {
            return emptyList()
        }

        return userService.search(SearchUserRequest(
                userIds = authorIds,
                limit = authorIds.size
        ))
    }

    private fun findStories(users: List<UserModel>, lastViewDateTime: Date): Map<Long, List<StoryModel>> {
        return storyService.search(SearchStoryRequest(
                live = true,
                status = StoryStatus.published,
                publishedStartDate = lastViewDateTime,
                userIds = users.map { it.id }
        )).groupBy { it.user.id }
    }
}

