package com.capitalone.dashboard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.capitalone.dashboard.auth.JwtAuthenticationFilter;
import com.capitalone.dashboard.auth.StandardLoginFilter;
import com.capitalone.dashboard.auth.TokenAuthenticationService;
import com.capitalone.dashboard.service.StandardAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Autowired
	private StandardAuthenticationProvider standardAuthenticationProvider;
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Bean
	public StandardLoginFilter standardLoginFilter() throws Exception {
		return new StandardLoginFilter("/login", authenticationManager(), tokenAuthenticationService);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().cacheControl();
		http.csrf().disable()
			.authorizeRequests().antMatchers("/appinfo").permitAll()
								.antMatchers("/login**").permitAll()
								.anyRequest().authenticated()
									.and()
								.addFilterBefore(standardLoginFilter(), UsernamePasswordAuthenticationFilter.class)
								.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
								.exceptionHandling().authenticationEntryPoint(new Http401AuthenticationEntryPoint("Authorization"));
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(standardAuthenticationProvider);
	}
	
}
