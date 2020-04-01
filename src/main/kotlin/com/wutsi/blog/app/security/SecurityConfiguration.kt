package com.wutsi.blog.app.security

import com.wutsi.blog.app.security.autologin.AutoLoginAuthenticationFilter
import com.wutsi.blog.app.security.autologin.AutoLoginAuthenticationProvider
import com.wutsi.blog.app.security.oauth.OAuthAuthenticationFilter
import com.wutsi.blog.app.security.oauth.OAuthAuthenticationProvider
import com.wutsi.blog.app.security.oauth.OAuthRememberMeService
import com.wutsi.blog.app.security.qa.QAAuthenticationFilter
import com.wutsi.blog.app.service.AccessTokenStorage
import com.wutsi.blog.app.util.CookieName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import javax.servlet.Filter


@Configuration
@EnableWebSecurity
class SecurityConfiguration(
        private val accessTokenStorage: AccessTokenStorage,
        private val logoutHandler: LogoutHandlerImpl,
        private val oauthAuthenticationProvider: OAuthAuthenticationProvider,
        private val oAuthRememberMeService: OAuthRememberMeService,
        private val autoLoginAuthenticationProvider: AutoLoginAuthenticationProvider
) : WebSecurityConfigurerAdapter() {
    companion object {
        const val OAUTH_SIGNIN_PATTERN = "/login/oauth/signin"
        const val QA_SIGNIN_PATTERN = "/login/qa/signin"

        const val SESSION_STATE = "wusti.social.state"

        const val PARAM_ACCESS_TOKEN = "token"
        const val PARAM_STATE = "state"
        const val PARAM_USER = "user"
        const val PARAM_PROVIDER = "provider"

        const val PROVIDER_GITHUB = "github"
        const val PROVIDER_FACEBOOK = "facebook"
        const val PROVIDER_GOOGLE = "google"
        const val PROVIDER_QA = "qa"
    }

    @Autowired
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(oauthAuthenticationProvider)
            .authenticationProvider(autoLoginAuthenticationProvider)
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // @formatter:off
		http
            .csrf().disable()

            .authorizeRequests()
                .antMatchers( "/").permitAll()
                .antMatchers( "*.ico").permitAll()
                .antMatchers( "/error").permitAll()
                .antMatchers( "/assets/**/*").permitAll()
                .antMatchers( "/actuator/**/*").permitAll()
                .antMatchers( "/login").permitAll()
                .antMatchers( "/login/**/*").permitAll()
                .antMatchers( "/logout").permitAll()
                .antMatchers( "/read/**/*").permitAll()
                .antMatchers( "/storage/**/*").permitAll()
                .anyRequest().authenticated()

            .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID", CookieName.ACCESS_TOKEN)
                    .addLogoutHandler(logoutHandler)

            .and()
                .formLogin()
                    .loginPage("/login").permitAll()
                    .defaultSuccessUrl("/")
		// @formatter:on
    }

    @Bean
    fun authenticationFilter(): OAuthAuthenticationFilter {
        val filter = OAuthAuthenticationFilter(OAUTH_SIGNIN_PATTERN)
        filter.setAuthenticationManager(authenticationManagerBean())
        filter.rememberMeServices = oAuthRememberMeService

        return filter
    }

    @Bean
    @ConditionalOnProperty(name=["wutsi.toggles.qa-login"], havingValue = "true")
    fun qaAuthenticationFilter(): QAAuthenticationFilter {
        val filter = QAAuthenticationFilter(QA_SIGNIN_PATTERN)
        filter.setAuthenticationManager(authenticationManagerBean())

        return filter
    }

    @Bean
    fun autoLoginAuthenticationFilter(): Filter = AutoLoginAuthenticationFilter(
            storage = accessTokenStorage,
            authenticationManager = authenticationManagerBean(),
            excludePaths = OrRequestMatcher(
                    AntPathRequestMatcher("/login"),
                    AntPathRequestMatcher("/login/**/*"),
                    AntPathRequestMatcher("/logout"),
                    AntPathRequestMatcher("/assets/**/*"),
                    AntPathRequestMatcher("*.ico")
            )
    )
}
