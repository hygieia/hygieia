package com.capitalone.dashboard.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletResponse;

import com.capitalone.dashboard.auth.apitoken.ApiTokenAuthenticationToken;
import com.capitalone.dashboard.auth.ldap.CustomUserDetails;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.service.BusCompOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.token.TokenAuthenticationService;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.service.UserInfoService;

@Component
public class DefaultAuthenticationResponseService implements AuthenticationResponseService {
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private BusCompOwnerService busCompOwnerService;
	
	@Override
	public void handle(HttpServletResponse response, Authentication authentication) {
		String firstName = "";
		String middleName = "";
		String lastName = "";
		String displayName = "";
		String emailAddress = "";
		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			firstName = ((CustomUserDetails) authentication.getPrincipal()).getFirstName();
			middleName = ((CustomUserDetails) authentication.getPrincipal()).getMiddleName();
			lastName = ((CustomUserDetails) authentication.getPrincipal()).getLastName();
			displayName = ((CustomUserDetails) authentication.getPrincipal()).getDisplayName();
			emailAddress = ((CustomUserDetails) authentication.getPrincipal()).getEmailAddress();
		}

		AbstractAuthenticationToken authenticationWithAuthorities = null;

		AuthType authType = (AuthType)authentication.getDetails();
        if (authType == AuthType.APIKEY) {
            Collection<UserRole> roles = new ArrayList<>();
            roles.add(UserRole.ROLE_API);

            authenticationWithAuthorities = new ApiTokenAuthenticationToken(authentication.getPrincipal(),
                    authentication.getCredentials(), createAuthorities(roles));
            authenticationWithAuthorities.setDetails(authentication.getDetails());
        } else {
            Collection<? extends GrantedAuthority> authorities =
                    userInfoService.getAuthorities(authentication.getName(), firstName, middleName, lastName, displayName, emailAddress, (AuthType)authentication.getDetails());
            authenticationWithAuthorities = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities);
            authenticationWithAuthorities.setDetails(authentication.getDetails());

            if(authType == AuthType.LDAP){
				busCompOwnerService.assignOwnerToDashboards(firstName, middleName, lastName, authentication);
			}
        }
		
		tokenAuthenticationService.addAuthentication(response, authenticationWithAuthorities);
        
	}

    private Collection<? extends GrantedAuthority> createAuthorities(Collection<UserRole> authorities) {
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        authorities.forEach(authority -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.name()));
        });

        return grantedAuthorities;
    }

}
