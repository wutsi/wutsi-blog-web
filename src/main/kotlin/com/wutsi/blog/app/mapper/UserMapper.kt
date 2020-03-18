package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.UserModel
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toUserModel(auth: Authentication): UserModel {
        val oauth = auth as OAuth2AuthenticationToken
        val provider = oauth.authorizedClientRegistrationId
        if (provider == "github") {
            return toUserModelFromGithub(oauth.principal.attributes, provider)
        }

        throw IllegalStateException("Provider not supported: $provider")
    }

    private fun toUserModelFromGithub(attrs: Map<String, Any>, provider: String) = UserModel(
            id = attrs["id"]!!.toString(),
            fullName = githubFullName(attrs),
            email = attrs["email"]?.toString(),
            pictureUrl = attrs["avatar_url"]?.toString(),
            provider = provider
    )

    private fun githubFullName(attrs: Map<String, Any>): String {
        val name = attrs["name"]?.toString()
        return if (name == null || name.isEmpty()) attrs["login"]!!.toString() else name
    }
}
