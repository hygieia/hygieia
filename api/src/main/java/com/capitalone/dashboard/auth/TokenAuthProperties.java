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
public class TokenAuthProperties {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthProperties.class);
	
	private Long expirationTime;
	private String secret;
	
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
