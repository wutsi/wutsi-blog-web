package com.wutsi.blog.app.security

import com.wutsi.blog.app.security.oauth.OAuthLogoutHandler
import com.wutsi.blog.app.util.CookieName
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@Configuration
@EnableWebSecurity
class SecurityConfiguration(
        private val accessTokenStorage: AccessTokenStorage,
        private val logoutHandler: OAuthLogoutHandler
) : WebSecurityConfigurerAdapter() {
    companion object {
        const val OAUTH_SIGNIN_PATTERN = "/login/oauth/signin"

        const val SESSION_STATE = "wusti.social.state"

        const val PARAM_ACCESS_TOKEN = "token"
        const val PARAM_STATE = "state"
        const val PARAM_USER = "user"
        const val PARAM_PROVIDER = "provider"

        const val PROVIDER_GITHUB = "github"
        const val PROVIDER_FACEBOOK = "facebook"
    }

    @Throws(Exception::class)
    public override fun configure(http: HttpSecurity) {
        // @formatter:off
		http
            .authorizeRequests()
                .antMatchers( "/").permitAll()
                .antMatchers( "/favicon.ico").permitAll()
                .antMatchers( "/error").permitAll()
                .antMatchers( "/assets/**/*").permitAll()
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

            .and()
                .csrf()
                    .disable()
		// @formatter:on
    }


    @Bean
    fun authenticationFilter(): AuthenticationFilter {
        val filter = AuthenticationFilter(accessTokenStorage, SecurityConfiguration.OAUTH_SIGNIN_PATTERN)
        filter.setAuthenticationManager(authenticationManagerBean())

        return filter
    }
}
