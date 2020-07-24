package com.wutsi.blog.app.page.payment

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.payment.service.EarningService
import com.wutsi.blog.app.util.PageName
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate

@Controller
@RequestMapping("/me/earning")
@ConditionalOnProperty(value="wutsi.toggles.earning", havingValue = "true")
class EarningController(
        private val service: EarningService,
        requestContext: RequestContext
): AbstractPageController( requestContext) {
    override fun pageName() = PageName.EARNING

    @GetMapping()
    fun index(
            @RequestParam(required = false) year: String ?= null,
            model: Model
    ): String {
        val currentYear = toYear(year)
        val nextYear = toYear(year)+1
        val previousYear = toYear(year)-1
        model.addAttribute("year", currentYear)
        model.addAttribute("nextYear", nextYear)
        model.addAttribute("nextYearUrl", "/earning?year=$nextYear")
        model.addAttribute("previousYear", previousYear)
        model.addAttribute("previousYearUrl", "/earning?year=$previousYear")

        val total = service.total(currentYear)
        if (!total.isEmpty()) {
            model.addAttribute("total", total)
        }
        return "page/payment/earning"
    }

    @ResponseBody
    @GetMapping(value=["/bar-chart-data"], produces = ["application/json"])
    fun barChartData(
            @RequestParam year: Int
    ): BarChartModel {
        return service.barChartData(year)
    }


    fun toYear(year: String?): Int {
        if (year == null){
            return LocalDate.now().year
        } else {
            try {
                return year.toInt()
            } catch (e: Exception){
                return LocalDate.now().year
            }
        }
    }
}
