package com.wutsi.blog.app.controller.home

import com.wutsi.blog.app.service.RequestContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.text.SimpleDateFormat
import java.util.Date
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping()
class PWAController(
        @Value("\${wutsi.base-url}") private val baseUrl: String,
        @Value("\${wutsi.asset-url}") private val assetUrl: String,
        @Value("\${wutsi.pwa.manifest.name}") private val name: String,
        private val requestContext: RequestContext,
        private val response: HttpServletResponse
) {
    private val lastModified: String = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(Date())

    @GetMapping("/sw.js", produces = ["text/javascript"])
    fun sw(): String {
        setLastModified()
        return "page/home/sw.js"
    }

    @GetMapping("/a2hs.js", produces = ["text/javascript"])
    fun a2hsjs(): String {
        setLastModified()
        return if (requestContext.toggles().addToHomeScreen) "page/home/a2hs.js" else "page/home/a2hs-disabled.js"
    }

    @GetMapping("/a2hs")
    fun a2hs(): String {
        setLastModified()
        return "page/home/a2hs"
    }


    @GetMapping("/manifest.json", produces = ["application/json"])
    @ResponseBody
    fun manifest(): Manifest {
        return Manifest(
                name = name,
                short_name = name,
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

    private fun setLastModified() {
        response.setHeader("Last-Modified", lastModified)
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
