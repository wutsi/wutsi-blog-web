package com.wutsi.blog.app.component.like

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.like.model.LikeCountModel
import com.wutsi.blog.app.component.like.model.LikeModel
import com.wutsi.blog.app.component.like.service.LikeService
import com.wutsi.blog.app.page.track.model.PushTrackForm
import com.wutsi.blog.app.page.track.service.TrackService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/like")
class LikeController(
    requestContext: RequestContext,
    private val likeService: LikeService,
    private val trackService: TrackService
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.LIKE

    @ResponseBody
    @PostMapping(produces = ["application/json"])
    fun like(
        @RequestParam storyId: Long,
        @RequestParam(required = false) likeId: Long? = null,
        @RequestParam(required = false) page: String? = null,
        @RequestParam(required = false) hitId: String? = null
    ): LikeModel {
        if (likeId != null) {
            likeService.delete(likeId)
            return LikeModel(id = likeId)
        } else {
            val result = likeService.create(storyId = storyId)
            track(hitId, page, storyId)
            return result
        }
    }

    private fun track(hitId: String?, page: String?, storyId: Long) {
        trackService.push(
            PushTrackForm(
                time = System.currentTimeMillis(),
                url = requestContext.request.getHeader("Referer"),
                event = "like",
                hid = hitId,
                page = page,
                pid = storyId.toString()
            )
        )
    }

    /**
     * Search all the likes of a list of stories, for the current user
     */
    @ResponseBody
    @GetMapping("/search", produces = ["application/json"])
    fun search(@RequestParam storyId: Array<Long>): List<LikeModel> {
        val userId = requestContext.currentSession()?.userId

        if (userId == null) {
            return emptyList()
        } else {
            return likeService.search(storyId.toList())
        }
    }

    /**
     * Return the number of likes for a list of stories
     */
    @ResponseBody
    @GetMapping("/count", produces = ["application/json"])
    fun count(@RequestParam storyId: Array<Long>): List<LikeCountModel> {
        return likeService.count(storyId.toList())
    }
}
