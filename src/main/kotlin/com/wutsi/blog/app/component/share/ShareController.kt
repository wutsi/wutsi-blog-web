package com.wutsi.blog.app.component.share

import com.wutsi.blog.app.component.share.model.ShareModel
import com.wutsi.blog.app.component.share.service.ShareService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/share")
class ShareController(
    private val shareService: ShareService
) {
    @ResponseBody
    @PostMapping(produces = ["application/json"])
    fun share(@RequestParam storyId: Long, @RequestParam target: String): ShareModel {
        val id = shareService.create(storyId, target)
        return ShareModel(id)
    }
}
