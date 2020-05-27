package com.wutsi.blog

import org.openqa.selenium.chrome.ChromeOptions


abstract class SeleniumMobileTestSupport: SeleniumTestSupport() {

    override fun driverOptions(): ChromeOptions {
        val options = super.driverOptions()
        options.setExperimentalOption("mobileEmulation", mapOf(
                "deviceName" to "Nexus 5"
        ))
        return options
    }

}
