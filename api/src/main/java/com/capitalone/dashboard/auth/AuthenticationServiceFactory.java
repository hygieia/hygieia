package com.capitalone.dashboard.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.service.AuthenticationService;

@Component
public class AuthenticationServiceFactory {

	private Map<AuthenticationScheme, AuthenticationService> serviceMap;
	
	@Autowired
	public AuthenticationServiceFactory(AuthenticationService authenticationService) {
		serviceMap = new HashMap<AuthenticationScheme, AuthenticationService>();
		serviceMap.put(AuthenticationScheme.STANDARD, authenticationService);
	}
	
	public AuthenticationService getAuthenticationService(AuthenticationScheme authenticationScheme) {
		AuthenticationService service = serviceMap.get(authenticationScheme);
		
		if(service == null) {
			return serviceMap.get(AuthenticationScheme.STANDARD);
		}
		
		return service;
	}
	
}
