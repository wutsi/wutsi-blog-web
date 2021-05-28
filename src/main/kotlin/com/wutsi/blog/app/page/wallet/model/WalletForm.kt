package com.wutsi.blog.app.page.wallet.model

import com.wutsi.blog.app.common.model.MobileProviderModel

public data class WalletForm(
    val mobileNumber: String = "",
    val mobileProvider: MobileProviderModel = MobileProviderModel.INVALID,
    val country: String = "",
    val fullName: String = ""
)
