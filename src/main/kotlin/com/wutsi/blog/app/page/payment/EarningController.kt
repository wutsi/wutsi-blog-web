package com.wutsi.blog.app.page.payment

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.model.tui.BarChartModel
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.partner.service.PartnerService
import com.wutsi.blog.app.page.payment.service.ContractService
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
@ConditionalOnProperty(value = ["wutsi.toggles.earning"], havingValue = "true")
class EarningController(
    private val service: EarningService,
    private val partners: PartnerService,
    private val contracts: ContractService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.EARNING

    @GetMapping()
    fun index(
        @RequestParam(required = false) year: String ? = null,
        model: Model
    ): String {
        val currentYear = toYear(year)
        loadYear(currentYear, model)
        loadTotal(currentYear, model)
        loadTotal(currentYear, model)

        model.addAttribute("joinWPP", shouldJoinWPP())
        return "page/payment/earning"
    }

    private fun loadYear(year: Int, model: Model) {
        val nextYear = year + 1
        val previousYear = year - 1
        model.addAttribute("year", year)
        model.addAttribute("nextYear", nextYear)
        model.addAttribute("nextYearUrl", "/me/earning?year=$nextYear")
        model.addAttribute("previousYear", previousYear)
        model.addAttribute("previousYearUrl", "/me/earning?year=$previousYear")
    }

    private fun loadTotal(year: Int, model: Model) {
        val total = service.total(year)
        if (total != null) {
            model.addAttribute("total", total)
        }
    }

    private fun shouldJoinWPP() = !contracts.hasContract() && !partners.isPartner()

    @ResponseBody
    @GetMapping(value = ["/bar-chart-data"], produces = ["application/json"])
    fun barChartData(
        @RequestParam year: Int
    ): BarChartModel {
        return service.barChartData(year)
    }

    fun toYear(year: String?): Int {
        if (year == null) {
            return LocalDate.now().year
        } else {
            try {
                return year.toInt()
            } catch (e: Exception) {
                return LocalDate.now().year
            }
        }
    }
}
