package com.wutsi.blog.app.component.survey

import com.wutsi.blog.app.common.model.SurveyModel
import com.wutsi.blog.app.component.announcement.model.AnnouncementResponse
import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.component.announcement.service.AnnouncementService
import com.wutsi.blog.app.component.survey.service.SurveyService
import com.wutsi.blog.app.util.CookieHelper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/survey")
class SurveyController(private val service: SurveyService) {
    @GetMapping()
    @ResponseBody
    fun index(request: HttpServletRequest): SurveyModel {
        if (!service.hasAlreadyAnswer(request)){
            return service.get()
        } else {
            return SurveyModel(active=false)
        }
    }

    @GetMapping("/click")
    @ResponseBody
    fun click(request: HttpServletRequest, response: HttpServletResponse): Map<String, String> {
        service.click(request, response)
        return emptyMap();
    }

}
