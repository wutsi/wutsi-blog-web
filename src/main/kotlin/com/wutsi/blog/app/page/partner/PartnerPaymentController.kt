package com.wutsi.blog.app.page.partner

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.page.partner.model.PartnerForm
import com.wutsi.blog.app.page.partner.service.PartnerService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.wpp.MobileProvider
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Locale


@Controller
@RequestMapping()
class PartnerPaymentController(
        private val service: PartnerService,
        requestContext: RequestContext
): AbstractPageController( requestContext) {
    override fun pageName() = PageName.PARTNER_PAYMENT

    @GetMapping("/partner/payment")
    fun index(model: Model): String {
        loadPartner(model)
        loadCountries(model)
        loadProviders(model)
        return "page/partner/payment"
    }

    @PostMapping("/partner/payment")
    fun submit(@ModelAttribute partner: PartnerForm, model: Model) : String {
        try {

            service.save(partner)
            return "redirect:/partner/success"

        } catch (ex: Exception) {
            val error = if (ex is NumberParseException) "error.invalid_mobile_number" else errorKey(ex)

            model.addAttribute("error", requestContext.getMessage(error))
            model.addAttribute("partner", partner)
            loadProviders(model)
            loadCountries(model)
            return "page/partner/payment"
        }
    }

    private fun loadPartner(model: Model){
        try {
            val partner = service.get()
            val util = PhoneNumberUtil.getInstance()
            val mobileNumber = util.parse(partner.mobileNumber, partner.countryCode)

            model.addAttribute("partner", PartnerForm(
                    fullName = partner.fullName,
                    email = partner.email,
                    mobileNumber = util.format(mobileNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL),
                    mobileProvider = partner.mobileProvider,
                    countryCode = partner.countryCode
            ))
        } catch (ex: Exception){
            val email = requestContext.currentUser()?.email
            model.addAttribute("partner", PartnerForm(
                    email = if (email == null) "" else email,
                    mobileNumber = "",
                    mobileProvider = MobileProvider.mtn,
                    countryCode = ""
            ))
        }
    }

    private fun loadCountries (model: Model) {
        val language = LocaleContextHolder.getLocale().language
        model.addAttribute("countries", arrayListOf(
                Locale(language, "CM")
        ))
    }

    private fun loadProviders (model: Model) {
        model.addAttribute("providers", arrayListOf(
                MobileProvider.mtn.name
        ))
    }
}
