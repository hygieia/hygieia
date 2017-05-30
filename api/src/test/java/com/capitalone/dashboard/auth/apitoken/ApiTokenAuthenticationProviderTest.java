package com.capitalone.dashboard.auth.apitoken;

import com.capitalone.dashboard.service.ApiTokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiTokenAuthenticationProviderTest {

    @Mock
    private ApiTokenService service;

    @InjectMocks
    private ApiTokenAuthenticationProvider provider;

    @Test
    public void shouldAuthenticate() {
        String username = "username";
        String password = "password";
        Authentication auth = new ApiTokenAuthenticationToken(username, password);
        when(service.authenticate(username, password)).thenReturn(auth);

        Authentication result = provider.authenticate(auth);

        assertSame(auth, result);
    }

    @Test
    public void shouldSupportStandardAuthToken() {
        assertTrue(provider.supports(ApiTokenAuthenticationToken.class));
        assertFalse(provider.supports(UsernamePasswordAuthenticationToken.class));
    }
}
