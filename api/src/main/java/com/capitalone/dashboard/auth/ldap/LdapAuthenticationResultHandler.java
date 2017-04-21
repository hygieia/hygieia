package com.capitalone.dashboard.auth.ldap;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.AuthProperties;
import com.capitalone.dashboard.auth.token.TokenAuthenticationResultHandler;
import com.capitalone.dashboard.auth.token.TokenAuthenticationService;
import com.capitalone.dashboard.model.UserRole;
import com.google.common.collect.Sets;

@Component
public class LdapAuthenticationResultHandler extends TokenAuthenticationResultHandler {
    
    private final AuthProperties authProperties;
    
    @Autowired
    public LdapAuthenticationResultHandler(TokenAuthenticationService tokenService, AuthProperties authProperties) {
        super(tokenService);
        this.authProperties = authProperties;
    }

    @Override
    protected Authentication beforeTokenCreate(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Collection<? extends GrantedAuthority> standardizedAuthorities = standardizeAuthorities(authentication.getAuthorities());
        UsernamePasswordAuthenticationToken standardizedAuth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), standardizedAuthorities);
        standardizedAuth.setDetails(authentication.getDetails()); 
        
        return standardizedAuth;
    }
    
    private Collection<? extends GrantedAuthority> standardizeAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Collection<GrantedAuthority> standardizedAuthorities = Sets.newHashSet();
        authorities.forEach(authority -> {
            if(authority.getAuthority().equals(authProperties.getLdapAdminGroup())) {
                standardizedAuthorities.add(new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.name()));
            }
        });
        
        return standardizedAuthorities;
    }

}
