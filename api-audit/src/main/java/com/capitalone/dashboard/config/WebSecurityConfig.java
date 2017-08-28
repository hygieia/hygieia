package com.capitalone.dashboard.config;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.auth.apitoken.ApiTokenAuthenticationProvider;
import com.capitalone.dashboard.auth.apitoken.ApiTokenRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationResultHandler authenticationResultHandler;

    @Autowired
    private ApiTokenAuthenticationProvider apiTokenAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().cacheControl();
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/ping").permitAll()
                .antMatchers("/swagger/**").permitAll()
                //TODO: sample call secured with ROLE_API
                //.antMatchers("/ping").hasAuthority("ROLE_API")
                //.antMatchers(HttpMethod.GET, "/**").permitAll()
                .antMatchers(HttpMethod.GET, "/v2/api-docs").permitAll()

                .anyRequest().authenticated()
                .and()
                .addFilterBefore(apiTokenRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(new Http401AuthenticationEntryPoint("Authorization"));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(apiTokenAuthenticationProvider);
    }

    @Bean
    protected ApiTokenRequestFilter apiTokenRequestFilter() throws Exception {
        return new ApiTokenRequestFilter("/**", authenticationManager(), authenticationResultHandler);
    }

}
