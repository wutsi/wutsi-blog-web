package com.wutsi.blog.app.page.wallet.model

import com.wutsi.blog.client.user.MobileProvider
import com.wutsi.blog.client.user.MobileProvider.INVALID

public data class WalletForm(
    public val mobileNumber: String = "",
    public val mobileProvider: MobileProvider = INVALID,
    public val country: String = "",
    public val fullName: String = ""
)
