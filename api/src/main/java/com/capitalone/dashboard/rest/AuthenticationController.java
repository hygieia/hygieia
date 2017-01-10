package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

import com.capitalone.dashboard.auth.SecurityService;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.request.AuthenticationRequest;
import com.capitalone.dashboard.service.AuthenticationService;


@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final SecurityService securityService;
    
    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, SecurityService securityService) {
        this.authenticationService = authenticationService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "/registerUser", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @Valid @RequestBody AuthenticationRequest request) {
    	try {
	    	Authentication authentication = authenticationService.create(request.getUsername(), request.getPassword());
	    	securityService.inflateResponse(httpServletResponse, authentication, AuthType.STANDARD);
	    	return ResponseEntity.ok().body(authentication.getName());
    	} catch (DuplicateKeyException dke) {
    		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("User already exists");
    	}
    }

    @RequestMapping(value = "/updateUser", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUser(@Valid @RequestBody AuthenticationRequest request) {
        // TODO: should return proper HTTP codes for not found users
        // TODO: should validate revalidate current password before allowing changes?
    	// TODO: should update based on security context and not passed in user and password
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.update(request.getUsername(), request.getPassword()));
    }
}
