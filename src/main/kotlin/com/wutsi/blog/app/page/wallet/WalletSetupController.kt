package com.wutsi.blog.app.page.wallet

import com.google.i18n.phonenumbers.NumberParseException
import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.model.MobileProviderModel
import com.wutsi.blog.app.common.model.MobileProviderModel.INVALID
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.service.UserMapper
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.wallet.model.WalletForm
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
import java.util.Locale

@Controller
@RequestMapping("/wallet/setup")
class WalletSetupController(
    private val service: UserService,
    private val mapper: UserMapper,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WalletSetupController::class.java)
    }

    override fun pageName() = PageName.WALLET_EDIT

    @GetMapping
    fun setup(
        @RequestParam(required = false) error: String? = null,
        @RequestParam(required = false) mobileNumber: String? = null,
        @RequestParam(required = false) mobileProvider: MobileProviderModel? = null,
        @RequestParam(required = false) country: String? = null,
        @RequestParam(required = false) fullName: String? = null,
        model: Model
    ): String {
        if (error != null)
            model.addAttribute("error", requestContext.getMessage(error))

        model.addAttribute("providers", getPaymentProviders())
        model.addAttribute("countries", getCountries())

        val user = requestContext.currentUser()
        val wallet = user?.wallet
        if (wallet == null) {
            model.addAttribute(
                "form",
                WalletForm(
                    fullName = fullName ?: (user?.fullName ?: ""),
                    mobileNumber = mobileNumber ?: "",
                    mobileProvider = mobileProvider ?: INVALID,
                    country = country ?: ""
                )
            )
        } else {
            model.addAttribute(
                "form",
                WalletForm(
                    fullName = fullName ?: wallet.fullName,
                    mobileNumber = mapper.formatMobileNumber(
                        mobileNumber ?: wallet.mobileNumber,
                        country ?: wallet.country
                    ),
                    mobileProvider = mobileProvider ?: wallet.mobileProvider,
                    country = country ?: wallet.country
                )
            )
        }
        return "page/wallet/setup"
    }

    @PostMapping
    fun save(@ModelAttribute form: WalletForm, model: Model): String {
        try {
            service.saveWallet(form)
            return "redirect:/me/settings?highlight=wallet-container#wallet"
        } catch (ex: Exception) {
            LOGGER.error("Unable to save the wallet", ex)

            val error = if (ex is NumberParseException) "error.invalid_mobile_number" else errorKey(ex)
            return "redirect:/wallet/setup?" +
                "&error=" + URLEncoder.encode(error, "utf-8") +
                "&mobileNumber=" + URLEncoder.encode(form.mobileNumber, "utf-8") +
                "&mobileProvider" + form.mobileProvider +
                "&country=" + form.country +
                "&fullName=" + URLEncoder.encode(form.fullName, "utf-8")
        }
    }

    private fun getPaymentProviders(): List<String> =
        arrayListOf(
            MobileProviderModel.MTN.name,
            MobileProviderModel.ORANGE.name
        )

    private fun getCountries(): List<Locale> {
        val language = requestContext.request.locale.language
        return arrayListOf(
            Locale(language, "CM")
        )
    }
}
