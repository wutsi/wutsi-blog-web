package com.wutsi.blog.app.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


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
                .antMatchers( "/favicon.ico").permitAll()
                .antMatchers( "/error").permitAll()
                .antMatchers( "/assets/**/*").permitAll()
                .antMatchers( "/login").permitAll()
                .antMatchers( "/logout").permitAll()
                .antMatchers( "/read/**/*").permitAll()
                .antMatchers( "/storage/**/*").permitAll()
                .antMatchers( HttpMethod.POST, "/upload").permitAll()
                .antMatchers( "/twitter/**/*").permitAll()
                .anyRequest().authenticated()
            .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
            .and()
                .oauth2Login()
                    .successHandler(loginSuccessHandler)
                    .loginPage("/login")
            .and()
                .csrf()
                    .disable()
		// @formatter:on
    }


}
