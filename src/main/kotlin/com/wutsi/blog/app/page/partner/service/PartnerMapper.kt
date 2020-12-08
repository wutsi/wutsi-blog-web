package com.wutsi.blog.app.page.partner.service

import com.wutsi.blog.app.page.partner.model.PartnerModel
import com.wutsi.blog.client.wpp.PartnerDto
import org.springframework.stereotype.Service

@Service
class PartnerMapper {
    fun toPartnerModel(partner: PartnerDto) = PartnerModel(
        id = partner.id,
        fullName = partner.fullName,
        mobileNumber = partner.mobileNumber,
        mobileProvider = partner.mobileProvider,
        countryCode = partner.countryCode,
        email = partner.email
    )
}
