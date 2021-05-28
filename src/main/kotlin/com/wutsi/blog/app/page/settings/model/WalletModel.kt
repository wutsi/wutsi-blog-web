package com.wutsi.blog.app.page.settings.model

import com.wutsi.blog.app.common.model.MobileProviderModel
import com.wutsi.blog.client.user.WalletType
import com.wutsi.blog.client.user.WalletType.INVALID

public data class WalletModel(
    public val type: WalletType = INVALID,
    public val mobileNumber: String = "",
    public val mobileProvider: MobileProviderModel = MobileProviderModel.INVALID,
    public val country: String = "",
    public val countryDisplayName: String = "",
    public val fullName: String = ""
)
