package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.capitalone.dashboard.model.LoginCredentials;
import com.capitalone.dashboard.request.AuthenticationRequest;
import com.capitalone.dashboard.service.AuthenticationService;


@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @RequestMapping(value = "/registerUser", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(HttpServletRequest httpServletRequest, @Valid @RequestBody AuthenticationRequest request) {
    	try {
    		String username = authenticationService.create(request.getUsername(), request.getPassword());
    		RestTemplate rest = new RestTemplate();
    		String url = httpServletRequest.getRequestURL().toString();
    		url = StringUtils.substringBefore(url, "/registerUser");
    		LoginCredentials loginCredentials = new LoginCredentials();
    		loginCredentials.setUsername(request.getUsername());
    		loginCredentials.setPassword(request.getPassword());
			ResponseEntity<Object> response = rest.postForEntity(url + "/login", loginCredentials, Object.class);
    		ResponseEntity<String> responseEntity = ResponseEntity.ok().headers(response.getHeaders()).body(username);
			return responseEntity;
    	} catch (DuplicateKeyException e) {
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
