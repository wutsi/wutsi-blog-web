package com.wutsi.blog.app.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository


@Configuration
@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var loginSuccessHandler: OAuth2LoginSuccessHandler

    @Throws(Exception::class)
    public override fun configure(http: HttpSecurity) {
        // @formatter:off
		http
            .authorizeRequests()
                .antMatchers( "/").permitAll()
                .antMatchers( "/error").permitAll()
                .antMatchers( "/assets/**/*").permitAll()
            .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
            .and()
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
                .oauth2Login()
                    .successHandler(loginSuccessHandler)
                    .loginPage("/login")

		// @formatter:on
    }


}
