package com.capitalone.dashboard.auth;

import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationResultHandlerTest {
	
	@Mock
	private AuthenticationResponseService responseService;
	
	@Mock
	private HttpServletResponse response;
	
	@Mock
	private Authentication auth;
	
	@InjectMocks
	private AuthenticationResultHandler handler;

	@Test
	public void testOnSucess() throws IOException, ServletException {
		handler.onAuthenticationSuccess(null, response, auth);
		
		verify(responseService).handle(response, auth);
	}

}
