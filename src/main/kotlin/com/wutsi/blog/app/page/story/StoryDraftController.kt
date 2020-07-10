package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/draft")
class StoryDraftController(
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryListController(service, requestContext) {
    override fun pageName() = PageName.STORY_DRAFT

    override fun viewName() = "page/story/draft"

    override fun fetchStories(limit: Int, offset: Int) = service.search(SearchStoryRequest(
            userId = requestContext.currentUser()?.id,
            status = StoryStatus.draft,
            sortBy = StorySortStrategy.modified,
            limit = limit,
            offset = offset
    ))
}
