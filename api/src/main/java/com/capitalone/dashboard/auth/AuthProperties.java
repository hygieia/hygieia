package com.capitalone.dashboard.auth;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthProperties.class);
	
	private Long expirationTime;
	private String secret;
	private String ldapUserDnPattern;
	private String ldapServerUrl;
	
	public void setExpirationTime(Long expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	public Long getExpirationTime() {
		return expirationTime;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public String getLdapUserDnPattern() {
		return ldapUserDnPattern;
	}

	public void setLdapUserDnPattern(String ldapUserDnPattern) {
		this.ldapUserDnPattern = ldapUserDnPattern;
	}

	public String getLdapServerUrl() {
		return ldapServerUrl;
	}

	public void setLdapServerUrl(String ldapServerUrl) {
		this.ldapServerUrl = ldapServerUrl;
	}

	@PostConstruct
	public void applyDefaultsIfNeeded() {
		if (getSecret() == null) {
			LOGGER.info("No JWT secret found in configuration, generating random secret by default.");
			setSecret(UUID.randomUUID().toString().replace("-", ""));			
		}
		
		if (getExpirationTime() == null) {
			LOGGER.info("No JWT expiration time found in configuration, setting to one day.");
			setExpirationTime((long) 1000*60*60*24);
		}
	}

}
