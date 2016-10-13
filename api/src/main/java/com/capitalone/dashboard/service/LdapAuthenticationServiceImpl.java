package com.capitalone.dashboard.service;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;

import com.capitalone.dashboard.config.LdapAuthConfigProperties;
import com.capitalone.dashboard.ldap.LdapTemplateBuilder;
import com.capitalone.dashboard.model.Authentication;

/**
 * Most functionality provided by the AuthenticationService has been removed.
 * 
 * Presumably the LDAP server used is pre-existing with means to add, delete, and update entries
 * externally from the Hygieia app. The LdapAuthenticationServiceImpl was designed to only
 * allow Ldap authentication, and not the modification of new/existing LDAP entries.
 */
public class LdapAuthenticationServiceImpl implements AuthenticationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapAuthenticationServiceImpl.class);
	
	private LdapAuthConfigProperties ldapAuthConfigProperties;
	
	@Autowired
	public LdapAuthenticationServiceImpl(LdapAuthConfigProperties ldapAuthConfigProperties) {
		this.ldapAuthConfigProperties = ldapAuthConfigProperties;
	}
	
	@Override
	public Iterable<Authentication> all() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Authentication get(ObjectId id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String create(String username, String password) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String update(String username, String password) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(ObjectId id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String username) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean authenticate(String username, String password) {
		LdapTemplate ldapTemplate = getLdapTemplateBuilder()
											.withUrl(ldapAuthConfigProperties.getUrl())
											.withDn(prepareDn(ldapAuthConfigProperties.getDn(), username))
											.withPassword(password)
											.build();
		if(ldapTemplate == null) {
			LOGGER.error("LDAP template was not able to be initialized. Please ensure user dn is specified correctly.");
			return false;
		}
		try {
			ldapTemplate.lookup(prepareDn(ldapAuthConfigProperties.getDn(), username));
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	private String prepareDn(String dn, Object... args) {
		return String.format(dn, args);
	}
	
	protected LdapTemplateBuilder getLdapTemplateBuilder() {
		return new LdapTemplateBuilder();
	}

}
