package com.capitalone.dashboard.auth.apitoken;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.model.AuthType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiTokenRequestFilterTest {

    @Mock
    private AuthenticationManager manager;

    @Mock
    private AuthenticationResultHandler resultHandler;

    private String path;

    private ApiTokenRequestFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Before
    public void setup() {
        path = "/**";
        filter = new ApiTokenRequestFilter(path, manager, resultHandler);
    }

    @Test
    public void shouldCreateFilter() {
        assertNotNull(filter);
    }

    @Test
    public void shouldAuthenticate() {
        when(request.getMethod()).thenReturn("POST");
        String principal = "somesys";
        String credentials = "itWuQ7y5zVKX1n+k8trjCNnx99o7AXbO";
        String authHdr = "Basic UGFzc3dvcmRJc0F1dGhUb2tlbjp7ImFwaUtleSI6Iml0V3VRN3k1elZLWDFuK2s4dHJqQ05ueDk5bzdBWGJPIn0K";
        when(request.getHeader("apiUser")).thenReturn(principal);
        when(request.getHeader("Authorization")).thenReturn(authHdr);
        Authentication auth = new ApiTokenAuthenticationToken(principal, authHdr);
        ArgumentCaptor<Authentication> argumentCaptor = ArgumentCaptor.forClass(Authentication.class);
        when(manager.authenticate(argumentCaptor.capture())).thenReturn(auth);

        Authentication result = filter.attemptAuthentication(request, response);

        assertNotNull(result);
        Authentication authentication = argumentCaptor.getValue();
        assertEquals(principal, authentication.getPrincipal());
        assertEquals(credentials, authentication.getCredentials());
        assertEquals(AuthType.APIKEY, authentication.getDetails());
    }

}