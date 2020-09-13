package com.wutsi.blog.app.page.like

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.comment.model.CreateCommentForm
import com.wutsi.blog.app.page.like.model.LikeCountModel
import com.wutsi.blog.app.page.like.model.LikeModel
import com.wutsi.blog.app.page.like.service.LikeService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/like")
class LikeController(
        requestContext: RequestContext,
        private val likes: LikeService
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.LIKE

    /**
     * Like or Unlike story
     */
    @ResponseBody
    @GetMapping(produces = ["application/json"])
    fun like(@RequestParam storyId: Long): LikeModel {
        return LikeModel(storyId=storyId)
    }


    /**
     * Search all the likes of a list of stories, for the current user
     */
    @ResponseBody
    @GetMapping("/search", produces = ["application/json"])
    fun search(@RequestParam storyId: Array<Long>): List<LikeModel> {
        val userId = requestContext.currentSession()?.userId
        val likes = likes.search(storyId.toList())
        if (userId == null){
            return emptyList()
        } else {
            return likes.map { LikeModel(
                    storyId = it.storyId,
                    userId = userId
            ) }
        }
    }


    /**
     * Return the number of likes for a list of stories
     */
    @ResponseBody
    @GetMapping("/count", produces = ["application/json"])
    fun count(@RequestParam storyId: Array<Long>): List<LikeCountModel> {
        return likes.count(storyId.toList())
    }

}
