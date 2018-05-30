package com.capitalone.dashboard.auth;

import com.capitalone.dashboard.auth.apitoken.ApiTokenAuthenticationToken;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserRole;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Component
public class DefaultAuthenticationResponseService implements AuthenticationResponseService {
	
	@Override
	public void handle(HttpServletResponse response, Authentication authentication) {

        AuthType authType = (AuthType)authentication.getDetails();
        if (authType == AuthType.APIKEY) {
            Collection<UserRole> roles = new ArrayList<>();
            roles.add(UserRole.ROLE_API);

            AbstractAuthenticationToken authenticationWithAuthorities = new ApiTokenAuthenticationToken(authentication.getPrincipal(),
                    authentication.getCredentials(), createAuthorities(roles));
            authenticationWithAuthorities.setDetails(authentication.getDetails());
        }
        
	}

    private Collection<? extends GrantedAuthority> createAuthorities(Collection<UserRole> authorities) {
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();
        authorities.forEach(authority -> grantedAuthorities.add(new SimpleGrantedAuthority(authority.name())));

        return grantedAuthorities;
    }

}
