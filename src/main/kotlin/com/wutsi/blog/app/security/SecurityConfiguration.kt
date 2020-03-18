package com.wutsi.blog.app.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@Configuration
@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var successHandler: OAuth2AuthenticationSuccessHandler

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
                    .deleteCookies("JSESSIONID")
            .and()
                .oauth2Login()
                    .successHandler(successHandler)
                    .loginPage("/login")
		// @formatter:on
    }


}
