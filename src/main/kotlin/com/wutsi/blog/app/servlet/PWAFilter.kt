package com.wutsi.blog.app.servlet

import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class PWAFilter : Filter {
    companion object {
        private val PWA_RESOURCES = arrayListOf<String>(
                "/manifest.json",
                "/sw.js",
                "/a2hs.js"
        )
    }
    private val lastModified: String = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(Date())

    override fun destroy() {
    }

    override fun init(config: FilterConfig?) {
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            if (accept(request as HttpServletRequest)){
                val resp = response as HttpServletResponse
                resp.setHeader("Last-Modified", lastModified)
            }
        } finally {
            chain.doFilter(request, response)
        }
    }

    private fun accept(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return PWA_RESOURCES.contains(path)
    }
}
