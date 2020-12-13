package com.wutsi.blog.app.page.partner.service

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.partner.model.PartnerForm
import com.wutsi.blog.app.page.partner.model.PartnerModel
import com.wutsi.blog.client.payment.SavePartnerRequest
import com.wutsi.blog.sdk.PartnerApi
import com.wutsi.core.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class PartnerService(
    private val api: PartnerApi,
    private val mapper: PartnerMapper,
    private val requestContext: RequestContext
) {

    fun isPartner(): Boolean {
        try {
            get()
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    fun get(): PartnerModel {
        val user = requestContext.currentUser()
            ?: throw NotFoundException("partner_not_found")

        val partner = api.get(user.id).partner
        return mapper.toPartnerModel(partner)
    }

    fun save(form: PartnerForm) {
        val user = requestContext.currentUser()
            ?: return

        val util = PhoneNumberUtil.getInstance()
        val mobileNumber = util.parse(form.mobileNumber, form.countryCode)
        if (!util.isValidNumber(mobileNumber)) {
            throw NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, form.mobileNumber)
        }
        api.save(
            user.id,
            SavePartnerRequest(
                fullName = form.fullName,
                countryCode = form.countryCode,
                mobileProvider = form.mobileProvider,
                mobileNumber = util.format(mobileNumber, E164),
                email = form.email
            )
        )
    }
}
