package com.capitalone.dashboard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.capitalone.dashboard.repository.AuthenticationRepository;
import com.capitalone.dashboard.service.AuthenticationService;
import com.capitalone.dashboard.service.AuthenticationServiceImpl;
import com.capitalone.dashboard.service.LdapAuthenticationServiceImpl;

@Configuration
@EnableConfigurationProperties
public class LdapAuthConfig {
	
	@Autowired
	private LdapAuthConfigProperties ldapAuthConfigProperties;
	
	@Autowired
	private AuthenticationRepository authenticationRepository;
	
	@Profile("ldapAuth")
	@Bean(name="authenticationService")
	public AuthenticationService authenticationServiceLdap() {
		return new LdapAuthenticationServiceImpl(ldapAuthConfigProperties);
	}
	
	@Profile("default")
	@Bean(name="authenticationService")
	public AuthenticationService authenticationServiceMongo() {
		return new AuthenticationServiceImpl(authenticationRepository);
	}
	
}
