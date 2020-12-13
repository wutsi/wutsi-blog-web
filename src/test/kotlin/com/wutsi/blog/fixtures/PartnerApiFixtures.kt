package com.wutsi.blog.fixtures

import com.wutsi.blog.client.payment.GetPartnerResponse
import com.wutsi.blog.client.payment.MobileProvider.mtn
import com.wutsi.blog.client.payment.PartnerDto
import com.wutsi.blog.client.payment.SavePartnerResponse

object PartnerApiFixtures {
    fun createPartnerDto(userId: Long, email: String, mobileNumber: String, fullName: String, country: String = "CM") = PartnerDto(
        id = System.currentTimeMillis(),
        email = email,
        mobileProvider = mtn,
        countryCode = country,
        fullName = fullName,
        userId = userId,
        mobileNumber = mobileNumber
    )

    fun createGetPartnerResponse(partner: PartnerDto) = GetPartnerResponse(partner=partner)

    fun createSavePartnerResponse() = SavePartnerResponse(
        partnerId = System.currentTimeMillis()
    )
}
