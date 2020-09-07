package com.wutsi.blog.app.page.like

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.like.model.LikeCountModel
import com.wutsi.blog.app.page.like.model.LikeModel
import com.wutsi.blog.app.util.PageName
import com.wutsi.core.util.NumberUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/like")
class LikeController(
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.LIKE

    /**
     * Like or Unlike story
     */
    @ResponseBody
    @GetMapping(produces = ["application/json"])
    fun like(@RequestParam storyId: Long): LikeModel {
        // Fake data
        return LikeModel(storyId=storyId)
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
            return storyId.map { LikeModel(
                    storyId = if (Math.random()*10 < 5) it else -1,
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
        // Fake data
        return storyId.map {
            LikeCountModel(
                    storyId = it,
                    value = 1000,
                    valueText = NumberUtils.toHumanReadable((10000*Math.random()).toLong())
            )
        }
    }

}
