package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.ViewBackend
import com.wutsi.blog.client.view.SearchViewRequest
import org.springframework.stereotype.Service

@Service
class ViewService(
        private val requestContext: RequestContext,
        private val backend: ViewBackend
) {
    fun search(storyIds: List<Long>): List<Long> {
        if (storyIds.isEmpty()){
            return emptyList()
        }

        return backend.search(SearchViewRequest(
                storyIds = storyIds,
                userId = requestContext.currentUser()?.id
        )).storyIds
    }
}

