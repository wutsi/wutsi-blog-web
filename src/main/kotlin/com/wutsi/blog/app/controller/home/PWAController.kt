package com.wutsi.blog.app.controller.home

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping()
class PWAController(
        @Value("\${wutsi.base-url}") private val baseUrl: String,
        @Value("\${wutsi.asset-url}") private val assetUrl: String
) {
    @GetMapping("/sw.js", produces = ["text/javascript"])
    fun sw(): String {
        return "page/home/sw.js"
    }

    @GetMapping("/manifest.json", produces = ["application/json"])
    @ResponseBody
    fun manifest(): Manifest {
        return Manifest(
                name = "Wutsi",
                short_name = "Wutsi",
                start_url = baseUrl,
                display = "standalone",
                background_color = "#f8f8f8",
                theme_color = "#f8f8f8",
                orientation = "portrait-primary",
                icons = arrayListOf(
                        Icon(
                                src ="$assetUrl/assets/wutsi/img/logo/logo_192x192.png",
                                type = "image/png",
                                sizes = "192x192"
                        ),
                        Icon(
                                src ="$assetUrl/assets/wutsi/img/logo/logo_512x512.png",
                                type = "image/png",
                                sizes = "512x512"
                        )
                )
        )
    }
}

data class Manifest (
        val name: String,
        val short_name: String,
        val start_url: String,
        val display: String,
        val background_color: String,
        val theme_color: String,
        val orientation: String,
        val icons: List<Icon>
);

data class Icon (
        val type: String,
        val sizes: String,
        val src: String
);
