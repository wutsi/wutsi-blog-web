package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.backend.PinBackend
import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextActionModel
import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.schemas.PersonSchemasGenerator
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.client.pin.PinDto
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Service
class PinMapper {
    fun toPinModel(obj: PinDto) = PinModel(
            id = obj.id,
            storyId = obj.storyId,
            userId = obj.userId
    )
}
