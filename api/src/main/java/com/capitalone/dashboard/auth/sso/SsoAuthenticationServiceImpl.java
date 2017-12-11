package com.capitalone.dashboard.auth.sso;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.ldap.CustomUserDetails;
import com.google.common.collect.Sets;

@Component
public class SsoAuthenticationServiceImpl implements SsoAuthenticationService {
	private static final Logger LOGGER = Logger.getLogger(SsoAuthenticationServiceImpl.class);

	@Autowired
	private SsoAuthenticationUtil ssoAuthenticationUtil;
	
	@Override
	public Authentication getAuthenticationFromHeaders(Map<String, String> requestHeadersMap) {
		Authentication authentication = this.getAuthenticationDataFromHeaders(requestHeadersMap);
		return authentication;
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Collection<String> roles) {
		Collection<GrantedAuthority> authorities = Sets.newHashSet();
		roles.forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role));
		});

		return authorities;
	}
	
	private Authentication getAuthenticationDataFromHeaders(Map<String, String> headersMap) {
		CustomUserDetails customUserDetails = null;
		try {
			if (headersMap != null) {
				String cookiesHeader = headersMap.get("cookiesheader");
				
				HashMap<String,String> userInfoDataMap = new ObjectMapper().readValue(cookiesHeader, HashMap.class);
				
				int count = 0;
				for(String header : headersMap.keySet()) {
					LOGGER.debug("Header (" + ++count + ".) : " + header + ", value : " + headersMap.get(header));
				}
				LOGGER.debug("cookiesHeader : ==> =====> =======>  " + cookiesHeader);
				
				customUserDetails = ssoAuthenticationUtil.createUser(userInfoDataMap);
				return ssoAuthenticationUtil.createSuccessfulAuthentication(customUserDetails);
			} else {
				LOGGER.error("SsoAuthenticationServiceImpl.getAuthenticationDataFromHeaders() :=> userInfo is Null");
			}
		} catch (Exception exception) {
			LOGGER.error("SsoAuthenticationServiceImpl.getAuthenticationDataFromHeaders() :=> Exception :"
					+ exception);
		}
		return null;
	}

}
