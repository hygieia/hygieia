package com.capitalone.dashboard.auth.apitoken;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ApiTokenAuthenticationTokenTest {
    private static final String CREDS = "creds";
    private static final String PRINCIPAL = "principal";

    @Test
    public void shouldCreateTokenNotAuthenticated() {
        Authentication auth = new ApiTokenAuthenticationToken(PRINCIPAL, CREDS);
        assertEquals(PRINCIPAL, auth.getPrincipal());
        assertEquals(CREDS, auth.getCredentials());
        assertFalse(auth.isAuthenticated());
    }

    @Test
    public void shouldCreateTokenAuthenticated() {
        Collection<? extends GrantedAuthority> authorities = Sets.newHashSet();
        Authentication auth = new ApiTokenAuthenticationToken(PRINCIPAL, CREDS, authorities);
        assertEquals(PRINCIPAL, auth.getPrincipal());
        assertEquals(CREDS, auth.getCredentials());
        assertTrue(auth.isAuthenticated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldSetAuthenticatedException() {
        Authentication auth = new ApiTokenAuthenticationToken(PRINCIPAL, CREDS);
        auth.setAuthenticated(true);
    }

    @Test
    public void shouldSetAuthenticatedToFalse() {
        Collection<? extends GrantedAuthority> authorities = Sets.newHashSet();
        Authentication auth = new ApiTokenAuthenticationToken(PRINCIPAL, CREDS, authorities);
        auth.setAuthenticated(false);
        assertFalse(auth.isAuthenticated());
    }

    @Test
    public void shouldEraseCreds() {
        Collection<? extends GrantedAuthority> authorities = Sets.newHashSet();
        ApiTokenAuthenticationToken auth = new ApiTokenAuthenticationToken(PRINCIPAL, CREDS, authorities);
        auth.eraseCredentials();
        assertNull(auth.getCredentials());
    }
}
