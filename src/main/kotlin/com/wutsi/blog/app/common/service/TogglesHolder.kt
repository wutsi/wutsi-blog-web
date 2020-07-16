package com.wutsi.blog.app.common.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@ConfigurationProperties(prefix = "wutsi.toggles")
class Toggles {
    var channel: Boolean = false
    var channelTwitter: Boolean = false
    var channelFacebook: Boolean = false
    var googleOneTapSignIn: Boolean = false
    var pwa: Boolean = false
    var recommendation: Boolean = false
    var imageKit: Boolean = false
    var shareButton: Boolean = true
    var ssoFacebook: Boolean = false
    var ssoGoogle: Boolean = false
    var ssoGithub: Boolean = false
    var ssoTwitter: Boolean = false
    var qaLogin: Boolean = false
    var wpp: Boolean = false
}

@Service
@EnableConfigurationProperties(Toggles::class)
class TogglesHolder(private val toggles: Toggles) {

    fun get() = toggles

}
