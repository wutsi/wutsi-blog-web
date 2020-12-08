package com.wutsi.blog.app.page.pwa

import com.wutsi.blog.app.backend.PushSubscriptionBackend
import com.wutsi.blog.app.page.pwa.model.IconModel
import com.wutsi.blog.app.page.pwa.model.ManifestModel
import com.wutsi.blog.client.channel.CreatePushSubscriptionRequest
import org.apache.commons.lang.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.text.SimpleDateFormat
import java.util.Date
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping()
class PWAController(
    private val backend: PushSubscriptionBackend,
    private val response: HttpServletResponse,

    @Value("\${wutsi.base-url}") private val baseUrl: String,
    @Value("\${wutsi.asset-url}") private val assetUrl: String,
    @Value("\${wutsi.pwa.manifest.name}") private val name: String,
    @Value("\${wutsi.pwa.firebase.sender-id}") private val senderId: String
) {
    private val lastModified = Date()
    private val maxAge = 86400 // 1 day

    @GetMapping("/sw.js", produces = ["text/javascript"])
    fun sw(): String {
        setUpCaching()
        return "page/pwa/sw.js"
    }

    @GetMapping("/a2hs.js", produces = ["text/javascript"])
    fun a2hsjs(): String {
        setUpCaching()
        return "page/pwa/a2hs.js"
    }

    @ResponseBody
    @GetMapping("/manifest.json", produces = ["application/json"])
    fun manifest(): ManifestModel {
        setUpCaching()
        return ManifestModel(
            name = name,
            short_name = name,
            start_url = "$baseUrl?utm_medium=pwa&utm_source=app",
            display = "standalone",
            background_color = "#f8f8f8",
            theme_color = "#f8f8f8",
            orientation = "portrait-primary",
            gcm_sender_id = senderId,
            icons = arrayListOf(
                IconModel(
                    src = "$assetUrl/assets/wutsi/img/logo/logo_192x192.png",
                    type = "image/png",
                    sizes = "192x192"
                ),
                IconModel(
                    src = "$assetUrl/assets/wutsi/img/logo/logo_512x512.png",
                    type = "image/png",
                    sizes = "512x512"
                )
            )
        )
    }

    @ResponseBody
    @PostMapping("/push/subscription", produces = ["application/json"])
    fun subscribe(@RequestBody form: Map<String, Any>): Map<String, Long> {
        val response = backend.create(CreatePushSubscriptionRequest(token = form["token"].toString()))
        return mapOf("id" to response.id)
    }

    private fun setUpCaching() {
        val fmt = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
        val expires = DateUtils.addSeconds(lastModified, maxAge)

        response.setHeader("Last-Modified", fmt.format(lastModified))
        response.setHeader("Expires", fmt.format(expires))
        response.setHeader("Cache-Control", "public, max-age=$maxAge")
    }
}
