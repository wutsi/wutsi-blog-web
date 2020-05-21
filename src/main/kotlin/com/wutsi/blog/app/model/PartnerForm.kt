package com.wutsi.blog.app.model

import com.wutsi.blog.client.wpp.MobileProvider

data class PartnerForm(
        val countryCode: String = "",
        val fullName: String = "",
        val mobileNumber: String = "",
        val mobileProvider: MobileProvider = MobileProvider.mtn,
        val email: String = ""
)
