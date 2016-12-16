package com.capitalone.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "muffin.authToken")
public class TokenAuthConfigProperties {

	private String expirationTime;
	private String secret;

	public long getExpirationTime() {
		return Long.valueOf(expirationTime);
	}

	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
