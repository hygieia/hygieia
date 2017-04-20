package com.capitalone.dashboard.auth;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
	private String ldapGroupSearchBase;
	private String adminLdapGroup;
	private String managerDn;
	private String managerPassword;
	
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
	
    public String getLdapGroupSearchBase() {
        return ldapGroupSearchBase;
    }

    public void setLdapGroupSearchBase(String ldapGroupSearchBase) {
        this.ldapGroupSearchBase = ldapGroupSearchBase;
    }

    public String getAdminLdapGroup() {
        return adminLdapGroup;
    }

    public void setAdminLdapGroup(String adminLdapGroup) {
        this.adminLdapGroup = adminLdapGroup;
    }

    public String getManagerDn() {
        return managerDn;
    }

    public void setManagerDn(String managerDn) {
        this.managerDn = managerDn;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public void setManagerPassword(String managerPassword) {
        this.managerPassword = managerPassword;
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
        
        if(StringUtils.isBlank(getManagerDn())) {
            setManagerDn(null);
        }
    }

}
