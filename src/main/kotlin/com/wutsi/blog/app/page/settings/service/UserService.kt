package com.wutsi.blog.app.page.settings.service

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.NumberParseException.ErrorType.NOT_A_NUMBER
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164
import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.model.UserAttributeForm
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.wallet.model.WalletForm
import com.wutsi.blog.client.user.MobileProvider
import com.wutsi.blog.client.user.MobileProvider.INVALID
import com.wutsi.blog.client.user.SaveWalletRequest
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import com.wutsi.blog.client.user.WalletType
import com.wutsi.blog.client.user.WalletType.MOBILE
import com.wutsi.blog.sdk.UserApi
import org.springframework.stereotype.Service

@Service
class UserService(
    private val api: UserApi,
    private val authBackend: AuthenticationBackend,
    private val mapper: UserMapper,
    private val requestContext: RequestContext

) {
    fun get(id: Long): UserModel {
        val user = api.get(id).user
        return mapper.toUserModel(user)
    }

    fun get(name: String): UserModel {
        val user = api.get(name).user
        return mapper.toUserModel(user)
    }

    fun getByAccessToken(accessToken: String): UserModel {
        val session = authBackend.session(accessToken).session
        return get(session.userId)
    }

    fun search(request: SearchUserRequest): List<UserModel> {
        val users = api.search(request).users
        return users.map { mapper.toUserModel(it) }
    }

    fun set(request: UserAttributeForm) {
        requestContext.currentUser() ?: return

        api.set(
            requestContext.currentUser()?.id!!,
            UpdateUserAttributeRequest(
                name = request.name,
                value = request.value.trim()
            )
        )
    }

    fun deactivateWallet() {
        val user = requestContext.currentUser() ?: return

        api.wallet(
            user.id,
            SaveWalletRequest(
                type = WalletType.INVALID,
                mobileProvider = INVALID,
                fullName = user.wallet?.fullName?.let { it } ?: "-",
                country = user.wallet?.country?.let { it } ?: "-",
                mobileNumber = user.wallet?.mobileNumber?.let { it } ?: "-"
            )
        )
    }

    fun saveWallet(form: WalletForm) {
        val user = requestContext.currentUser() ?: return

        val util = PhoneNumberUtil.getInstance()
        val mobileNumber = util.parse(form.mobileNumber, form.country)
        if (!util.isValidNumber(mobileNumber))
            throw NumberParseException(NOT_A_NUMBER, form.mobileNumber)

        api.wallet(
            user.id,
            SaveWalletRequest(
                type = MOBILE,
                mobileProvider = MobileProvider.valueOf(form.mobileProvider.name),
                fullName = form.fullName,
                country = form.country,
                mobileNumber = util.format(mobileNumber, E164)
            )
        )
    }
}
