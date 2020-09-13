package com.wutsi.blog.app.page.like

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
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
        private val likeService: LikeService
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.LIKE

    /**
     * Like or Unlike story
     */
    @ResponseBody
    @GetMapping(produces = ["application/json"])
    fun like(@RequestParam storyId: Long) {
        val likes = likeService.search(listOf(storyId))

        if(likes.isEmpty()){
            likeService.create(storyId=storyId)
        } else {
            likeService.delete(likes.get(0).id)
        }
    }


    /**
     * Search all the likes of a list of stories, for the current user
     */
    @ResponseBody
    @GetMapping("/search", produces = ["application/json"])
    fun search(@RequestParam storyId: Array<Long>): List<LikeModel> {
        val userId = requestContext.currentSession()?.userId

        if (userId == null){
            return emptyList()
        } else {
            val likes = likeService.search(storyId.toList())
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
        return likeService.count(storyId.toList())
    }

}
