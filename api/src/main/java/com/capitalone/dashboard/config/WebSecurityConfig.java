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
import com.capitalone.dashboard.auth.JwtLoginFilter;
import com.capitalone.dashboard.auth.TokenAuthenticationService;
import com.capitalone.dashboard.auth.TokenAuthenticationServiceImpl;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private TokenAuthConfigProperties tokenAuthConfigProperties;
	
	@Bean
	public TokenAuthenticationService tokenAuthenticationService(){
		return new TokenAuthenticationServiceImpl(tokenAuthConfigProperties.getExpirationTime(), tokenAuthConfigProperties.getSecret());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().cacheControl();
		http.csrf().disable()
			.authorizeRequests().antMatchers("/appinfo").permitAll()
								.antMatchers("/login").permitAll()
								.anyRequest().authenticated()
									.and()
								.addFilterBefore(new JwtLoginFilter("/login", authenticationManager(), tokenAuthenticationService()),
										UsernamePasswordAuthenticationFilter.class)
								.addFilterBefore(new JwtAuthenticationFilter(tokenAuthenticationService()), UsernamePasswordAuthenticationFilter.class)
								.exceptionHandling().authenticationEntryPoint(new Http401AuthenticationEntryPoint("Authorization"));
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN");
		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
		auth.inMemoryAuthentication().withUser("user2").password("password").roles("USER");
	}
	
}
