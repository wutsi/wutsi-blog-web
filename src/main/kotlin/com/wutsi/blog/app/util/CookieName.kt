package com.wutsi.blog.app.util

import com.wutsi.core.tracking.DeviceUIDProvider

class CookieName{
    companion object {
        const val ACCESS_TOKEN = "__w_tk"
        const val REFERER = "__w_rfr"
        const val READ_ALL_LANGUAGE = "__w_lang_all"
        const val DEVICE_UID = DeviceUIDProvider.COOKIE_NAME
    }
}
