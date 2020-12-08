package com.wutsi.blog.app.component.announcement

import com.wutsi.blog.app.component.announcement.model.AnnouncementResponse
import com.wutsi.blog.app.component.announcement.service.AnnouncementService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/announcement")
class AnnouncementController(private val service: AnnouncementService) {

    @GetMapping()
    @ResponseBody
    fun index(@RequestParam page: String, request: HttpServletRequest, response: HttpServletResponse): AnnouncementResponse {
        return service.get(page, request, response)
    }
}
