package com.wutsi.blog.app.page.wallet

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/wallet")
class WalletController(
    private val service: UserService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WalletController::class.java)
    }

    override fun pageName() = PageName.WALLET

    @GetMapping
    fun index(model: Model): String {
        val wallet = requestContext.currentUser()?.wallet
        if (wallet != null) {
            model.addAttribute("wallet", wallet)
        }
        return "page/wallet/index"
    }

    @GetMapping("/deactivate")
    fun deactivate(model: Model): String {
        try {
            val partnerId = requestContext.currentUser()?.id
            if (partnerId != null)
                service.deactivateWallet()
        } catch (ex: Exception) {
            LOGGER.error("Unable to deactivate your wallet", ex)
        }
        return "redirect:/me/settings?highlight=wallet-container#wallet"
    }
}
