package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.PushTrackForm
import com.wutsi.blog.app.service.TrackService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/track")
class TrackController(private val service: TrackService) {

    @ResponseBody
    @PostMapping(produces = ["application/json"], consumes = ["application/json"])
    fun track(@RequestBody form: PushTrackForm): Map<String, String> {
        val id = service.push(form)
        return mapOf("transactionId" to id)
    }

}
