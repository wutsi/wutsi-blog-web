package com.wutsi.blog.app.page.monetization

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.monetization.model.PlanForm
import com.wutsi.blog.app.page.monetization.service.MonetizationService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder

@Controller
@RequestMapping("/me/monetization/setup")
class MonetizationSetupController(
    private val service: MonetizationService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MonetizationSetupController::class.java)
        private const val NAME = "Default"
    }

    override fun pageName() = PageName.MONETIZATION_EDIT

    @GetMapping
    fun setup(
        @RequestParam(required = false) yearly: String? = null,
        @RequestParam(required = false) description: String? = null,
        @RequestParam(required = false) error: String? = null,
        model: Model
    ): String {
        val plan = service.currentPlan()
        model.addAttribute("error", error)
        if (plan != null) {
            model.addAttribute(
                "form", PlanForm(
                    id = plan.id,
                    name = NAME,
                    description = description ?: plan.description,
                    yearly = yearly ?: if (plan.rate.yearly.isFree()) "" else plan.rate.yearly.value.toString(),
                    currency = plan.rate.yearly.currency
                )
            )
        } else {
            model.addAttribute(
                "form", PlanForm(
                    name = NAME,
                    currency = requestContext.currency()
                )
            )
        }
        return "page/monetization/setup"
    }

    @PostMapping
    fun setup(@ModelAttribute form: PlanForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/me/settings?highlight=monetization-container#monetization"
        } catch (ex: Exception) {
            LOGGER.error("Unable to setup monetization", ex)
            val error = requestContext.getMessage("error.unexpected")
            return "redirect:/me/monetization/setup?yearly=${form.yearly}" +
                "&error=" + URLEncoder.encode(error, "utf-8") +
                "&description=" + URLEncoder.encode(form.description, "utf-8")
        }
    }
}
