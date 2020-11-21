package com.wutsi.blog.app.page.settings

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.settings.model.UserAttributeForm
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.awt.im.InputMethodHighlight

@Controller
@RequestMapping("/me/settings")
class SettingsController(
        private val userService: UserService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS

    @GetMapping
    fun index(
            @RequestParam(required = false) highlight: String? = null,
            model: Model
    ): String {
        model.addAttribute("highlight", highlight)
        return "page/settings/index"
    }

    @ResponseBody
    @PostMapping(produces = ["application/json"], consumes = ["application/json"])
    fun set(@RequestBody request: UserAttributeForm): Map<String, Any?> {
        try {

            userService.set(request)
            return mapOf("id" to requestContext.currentUser()?.id)

        } catch (ex: Exception) {
            val key = errorKey(ex)
            return mapOf(
                    "id" to requestContext.currentUser()?.id,
                    "error" to requestContext.getMessage(key)
            )
        }
    }
}
