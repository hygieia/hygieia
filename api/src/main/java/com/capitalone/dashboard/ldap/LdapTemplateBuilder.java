package com.capitalone.dashboard.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.LdapContextSource;

public class LdapTemplateBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapTemplateBuilder.class);
	
	private LdapContextSource ldapContextSource;
	
	public LdapTemplateBuilder() {
		ldapContextSource = new LdapContextSource();
		ldapContextSource.setDirObjectFactory(DefaultDirObjectFactory.class);
	}

	public LdapTemplateBuilder withUrl(String url) {
		ldapContextSource.setUrl(url);
		return this;
	}
	
	public LdapTemplateBuilder withDn(String dn, Object... args) {
		ldapContextSource.setUserDn(String.format(dn, args));
		return this;
	}
	
	public LdapTemplateBuilder withPassword(String password) {
		ldapContextSource.setPassword(password);
		return this;
	}
	
	public LdapTemplate build() {
		try {
			ldapContextSource.afterPropertiesSet();
			return new LdapTemplate(ldapContextSource);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
}
