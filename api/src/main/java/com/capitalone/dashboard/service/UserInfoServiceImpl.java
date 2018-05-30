package com.capitalone.dashboard.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import com.capitalone.dashboard.auth.AuthProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.exceptions.DeleteLastAdminException;
import com.capitalone.dashboard.auth.exceptions.UserNotFoundException;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.google.common.collect.Sets;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

@Component
public class UserInfoServiceImpl implements UserInfoService {

	private static final Logger LOGGER = Logger.getLogger(UserInfoServiceImpl.class);

	private UserInfoRepository userInfoRepository;
	@Autowired
	private AuthProperties authProperties;
	
	@Autowired
	public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
		this.userInfoRepository = userInfoRepository;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(String username, String firstName, String middleName, String lastName, String displayName, String emailAddress, AuthType authType) {
		Collection<UserRole> roles = getUserInfo(username, firstName, middleName, lastName, displayName, emailAddress, authType).getAuthorities();
		return createAuthorities(roles);
	}
	
	@Override
	public UserInfo getUserInfo(String username, String firstName, String middleName, String lastName, String displayName, String emailAddress, AuthType authType) {
		UserInfo userInfo = userInfoRepository.findByUsernameAndAuthType(username, authType);
		if(userInfo == null) {
			userInfo = createUserInfo(username, firstName, middleName, lastName, displayName, emailAddress, authType);
			userInfoRepository.save(userInfo);
		}
		
		// TODO: This will give the standard "admin" user admin privledges, might want
		// to bootstrap in an admin user, or something better than this.
		addAdminRoleToStandardAdminUser(userInfo);
		
		return userInfo;
	}
	
    @Override
    public Collection<UserInfo> getUsers() {
        return Sets.newHashSet(userInfoRepository.findAll());
    }

    @Override
    public UserInfo promoteToAdmin(String username, AuthType authType) {
        UserInfo user = userInfoRepository.findByUsernameAndAuthType(username, authType);
        if (user == null) {
            throw new UserNotFoundException(username, authType);
        }
        
        user.getAuthorities().add(UserRole.ROLE_ADMIN);
        UserInfo savedUser = userInfoRepository.save(user);
        return savedUser;
    }
    
    @Override
    public UserInfo demoteFromAdmin(String username, AuthType authType) {
        int numberOfAdmins = userInfoRepository.findByAuthoritiesIn(UserRole.ROLE_ADMIN).size();
        if(numberOfAdmins <= 1) {
            throw new DeleteLastAdminException();
        }
        UserInfo user = userInfoRepository.findByUsernameAndAuthType(username, authType);
        if (user == null) {
            throw new UserNotFoundException(username, authType);
        }
        
        user.getAuthorities().remove(UserRole.ROLE_ADMIN);
        UserInfo savedUser = userInfoRepository.save(user);
        return savedUser;
    }

	private UserInfo createUserInfo(String username, String firstName, String middleName, String lastName, String displayName, String emailAddress, AuthType authType) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(username);
		userInfo.setFirstName(firstName);
		userInfo.setMiddleName(middleName);
		userInfo.setLastName(lastName);
		userInfo.setDisplayName(displayName);
		userInfo.setEmailAddress(emailAddress);
		userInfo.setAuthType(authType);
		userInfo.setAuthorities(Sets.newHashSet(UserRole.ROLE_USER));
		
		return userInfo;
	}

	private void addAdminRoleToStandardAdminUser(UserInfo userInfo) {
		if ("admin".equals(userInfo.getUsername()) && AuthType.STANDARD == userInfo.getAuthType()) {
			userInfo.getAuthorities().add(UserRole.ROLE_ADMIN);
		}
	}
	
	private Collection<? extends GrantedAuthority> createAuthorities(Collection<UserRole> authorities) {
		Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		authorities.forEach(authority -> {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.name())); 
		});
		
		return grantedAuthorities;
	}

	/**
	 * Can be called to check validity of userId when creating a dashboard remotely via api
	 * @param userId
	 * @param authType
	 * @return
	 */
	@Override
	public boolean isUserValid(String userId, AuthType authType) {
		if (userInfoRepository.findByUsernameAndAuthType(userId, authType) != null) {
			return true;
		} else {
			if (authType == AuthType.LDAP) {
				try {
					return searchLdapUser(userId);
				} catch (NamingException ne) {
					LOGGER.error("Failed to query ldap for " + userId, ne);
					return false;
				}
			} else {
				return false;
			}
		}
	}

	private boolean searchLdapUser(String searchId) throws NamingException {
		boolean searchResult = false;

		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		props.put("java.naming.security.protocol", "ssl");
		props.put(Context.SECURITY_AUTHENTICATION, "simple");

		try {
			if (!StringUtils.isBlank(authProperties.getAdUrl())) {
				props.put(Context.PROVIDER_URL, authProperties.getAdUrl());
				props.put(Context.SECURITY_PRINCIPAL, authProperties.getLdapBindUser() + "@" + authProperties.getAdDomain());
			} else {
				props.put(Context.PROVIDER_URL, authProperties.getLdapServerUrl());
				props.put(Context.SECURITY_PRINCIPAL, StringUtils.replace(authProperties.getLdapUserDnPattern(), "{0}", authProperties.getLdapBindUser()));
			}
			props.put(Context.SECURITY_CREDENTIALS, authProperties.getLdapBindPass());
		} catch (Exception e) {
			LOGGER.error("Failed to retrieve properties for InitialDirContext", e);
			return false;
		}

		InitialDirContext context = new InitialDirContext(props);

		try {
			SearchControls ctrls = new SearchControls();
			ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			String searchBase = "";
			String searchFilter = "";
			if(!StringUtils.isBlank(authProperties.getAdUrl())) {
				searchBase = authProperties.getAdRootDn();
				searchFilter = "(&(objectClass=user)(userPrincipalName="	+ searchId + "@" + authProperties.getAdDomain() + "))";
			} else {
				searchBase = authProperties.getLdapUserDnPattern().substring(
						authProperties.getLdapUserDnPattern().indexOf(',') + 1,
						authProperties.getLdapUserDnPattern().length()
				);
				searchFilter = "(&(objectClass=user)(sAMAccountName="	+ searchId + "))";
			}

			NamingEnumeration<SearchResult> results = context.search(searchBase, searchFilter, ctrls);

			if (!results.hasMore()) {
				return searchResult;
			}

			SearchResult result = results.next();

			Attribute memberOf = result.getAttributes().get("memberOf");
			if (memberOf != null) {
				searchResult = true;
			}
		} finally {
			context.close();
		}

		return searchResult;
	}
}
