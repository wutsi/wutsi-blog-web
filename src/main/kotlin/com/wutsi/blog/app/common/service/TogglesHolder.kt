package com.wutsi.blog.app.common.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@ConfigurationProperties(prefix = "wutsi.toggles")
class Toggles {
    var announcement: Boolean = false
    var channel: Boolean = false
    var channelTwitter: Boolean = false
    var channelFacebook: Boolean = false
    var comment: Boolean = false
    var earning: Boolean = false
    var googleAdSense: Boolean = false
    var googleOneTapSignIn: Boolean = false
    var imageKit: Boolean = false
    var like: Boolean = false
    var pwa: Boolean = false
    var pwaBadge: Boolean = false
    var pwaPushNotification: Boolean = false
    var recommendation: Boolean = false
    var translation: Boolean = false
    var ssoFacebook: Boolean = false
    var ssoGoogle: Boolean = false
    var ssoGithub: Boolean = false
    var ssoTwitter: Boolean = false
    var survey: Boolean = false
    var qaLogin: Boolean = false
    var valueProp: Boolean = false
    var wpp: Boolean = false
}

@Service
@EnableConfigurationProperties(Toggles::class)
class TogglesHolder(private val toggles: Toggles) {

    fun get() = toggles

}
