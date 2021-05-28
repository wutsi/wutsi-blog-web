package com.wutsi.blog.app.page.monetization

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.monetization.service.MonetizationService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/monetization")
class MonetizationController(
    private val service: MonetizationService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MonetizationController::class.java)
    }

    override fun pageName() = PageName.MONETIZATION

    @GetMapping
    fun index(model: Model): String {
        val partnerId = requestContext.currentUser()?.id
        if (partnerId != null) {
            val plan = service.currentPlan(partnerId)
            if (plan != null)
                model.addAttribute("plan", plan)
        }
        return "page/monetization/index"
    }

    @GetMapping("/deactivate")
    fun deactivate(model: Model): String {
        try {
            val partnerId = requestContext.currentUser()?.id
            if (partnerId != null)
                service.deactivatePlan(partnerId)
        } catch (ex: Exception) {
            LOGGER.error("Unable to deactivate monetization", ex)
        }
        return "redirect:/me/settings?highlight=monetization-container#monetization"
    }
}
