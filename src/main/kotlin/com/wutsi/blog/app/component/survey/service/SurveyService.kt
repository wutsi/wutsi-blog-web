package com.wutsi.blog.app.component.survey.service

import com.wutsi.blog.app.common.model.SurveyModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.Toggles
import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.util.CookieHelper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import java.time.LocalDate
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class SurveyService(
        private val toggles: Toggles,
        private val requestContext: RequestContext,

        @Value("\${wutsi.survey.id}") private val surveyId: String,
        @Value("\${wutsi.survey.url}") private val surveyUrl: String,
        @Value("\${wutsi.survey.start-date}") private val surveyStartDate: String,
        @Value("\${wutsi.survey.end-date}") private val surveyEndDate: String
) {
    companion object{
        const val COOKIE_NAME = "__w_survey"
    }

    fun get(): SurveyModel {
        if (toggles.survey) {
            val survey = SurveyModel(
                    id = surveyId,
                    url = surveyUrl,
                    startDate = LocalDate.parse(surveyStartDate),
                    endDate = LocalDate.parse(surveyEndDate),
                    active = true
            )
            val now = LocalDate.now()
            if (now.isAfter(survey.startDate) && now.isBefore(survey.endDate)){
                return survey
            }
        }
        return SurveyModel(active=false)
    }

    fun click(request: HttpServletRequest, response: HttpServletResponse) {
        val survey = get()
        if (survey.active) {
            CookieHelper.put(COOKIE_NAME, "1", request, response, 30*CookieHelper.ONE_DAY_SECONDS)
        } else {
            CookieHelper.remove(COOKIE_NAME, response)
        }
    }

    fun hasAlreadyAnswer(request: HttpServletRequest) = CookieHelper.get(COOKIE_NAME, request) != null
}

