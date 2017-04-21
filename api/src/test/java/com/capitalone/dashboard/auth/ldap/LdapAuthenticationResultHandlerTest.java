package com.capitalone.dashboard.auth.ldap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.capitalone.dashboard.auth.AuthProperties;
import com.capitalone.dashboard.auth.token.TokenAuthenticationService;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class LdapAuthenticationResultHandlerTest {
    
    @Mock
    private TokenAuthenticationService tokenService;
    
    @Mock
    private AuthProperties authProps;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @InjectMocks
    private LdapAuthenticationResultHandler ldapResultHandler;
    
    ArgumentCaptor<Authentication> argumentCaptor = ArgumentCaptor.forClass(Authentication.class);

    @Test
    public void shouldStandardizeRolesForAdmin() throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = Lists.newArrayList(new SimpleGrantedAuthority("ROLE_HYGIEIA_ADMIN"));
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "password", authorities);
        when(authProps.getAdminLdapGroup()).thenReturn("ROLE_HYGIEIA_ADMIN");
        
        ldapResultHandler.onAuthenticationSuccess(request, response, auth);
        
        verify(tokenService).addAuthentication(any(HttpServletResponse.class), argumentCaptor.capture());
        assertTrue(argumentCaptor.getValue().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
    
    @Test
    public void shouldStandardizeRolesForNonAdmin() throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = Lists.newArrayList(new SimpleGrantedAuthority("ROLE_HYGIEIA_USER"));
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "password", authorities);
        when(authProps.getAdminLdapGroup()).thenReturn("ROLE_HYGIEIA_ADMIN");
        
        ldapResultHandler.onAuthenticationSuccess(request, response, auth);
        
        verify(tokenService).addAuthentication(any(HttpServletResponse.class), argumentCaptor.capture());
        assertFalse(argumentCaptor.getValue().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

}
