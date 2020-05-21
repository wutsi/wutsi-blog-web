package com.wutsi.blog.app.service

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.blog.app.backend.PartnerBackend
import com.wutsi.blog.app.mapper.PartnerMapper
import com.wutsi.blog.app.model.PartnerForm
import com.wutsi.blog.app.model.PartnerModel
import com.wutsi.blog.client.wpp.SavePartnerRequest
import org.springframework.stereotype.Service

@Service
class PartnerService(
        private val backend: PartnerBackend,
        private val mapper: PartnerMapper
) {
    fun get(): PartnerModel {
        val partner = backend.get().partner
        return mapper.toPartnerModel(partner)
    }

    fun save(form: PartnerForm) {
        val util = PhoneNumberUtil.getInstance()
        val mobileNumber = util.parse(form.mobileNumber, form.countryCode)
        if (!util.isValidNumber(mobileNumber)){
            throw NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, form.mobileNumber)
        }
        val request = SavePartnerRequest(
                fullName = form.fullName,
                countryCode = form.countryCode,
                mobileProvider = form.mobileProvider,
                mobileNumber = util.format(mobileNumber, PhoneNumberUtil.PhoneNumberFormat.E164),
                email = form.email
        )
        backend.save(request)
    }
}

