package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.auth.AuthProperties;
import com.capitalone.dashboard.auth.AuthenticationResponseService;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.request.AuthenticationRequest;
import com.capitalone.dashboard.service.AuthenticationService;


@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    
    private final AuthenticationResponseService authenticationResponseService;
    
    private final AuthProperties authProperties;
    
    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, AuthenticationResponseService authenticationResponseService, AuthProperties authProperties) {
        this.authenticationService = authenticationService;
        this.authenticationResponseService = authenticationResponseService;
        this.authProperties = authProperties;
    }

    @RequestMapping(value = "/registerUser", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> registerUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @Valid @RequestBody AuthenticationRequest request) throws IOException, ServletException {
	    	try {
		    	Authentication authentication = authenticationService.create(request.getUsername(), request.getPassword());
		    	authenticationResponseService.handle(httpServletResponse, authentication);
		    	return ResponseEntity.ok().build();
	    	} catch (DuplicateKeyException dke) {
	    		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
	    	}
    }

    @RequestMapping(value = "/updateUser", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUser(@Valid @RequestBody AuthenticationRequest request) {
        // TODO: should return proper HTTP codes for not found users
        // TODO: should validate revalidate current password before allowing changes?
    	// TODO: should update based on security context and not passed in user and password
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.update(request.getUsername(), request.getPassword()));
    }
    
    @RequestMapping(value = "/authenticationProviders", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<AuthType> getAuthenticationProviders() {
        return authProperties.getAuthenticationProviders();
    }
}
