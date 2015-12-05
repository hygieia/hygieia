package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.request.AuthenticationRequest;
import com.capitalone.dashboard.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @RequestMapping(value = "/authenticateUser", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> authenticateUser(@Valid @RequestBody AuthenticationRequest request) {
        // TODO: should return proper HTTP codes for invalid creds
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authenticationService.authenticate(request.getUsername(), request.getPassword()));
    }

    @RequestMapping(value = "/registerUser", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(@Valid @RequestBody AuthenticationRequest request) {
        // TODO: should return proper HTTP codes for existing users
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.create(request.getUsername(), request.getPassword()));
    }

    @RequestMapping(value = "/updateUser", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUser(@Valid @RequestBody AuthenticationRequest request) {
        // TODO: should return proper HTTP codes for not found users
        // TODO: should validate revalidate current password before allowing changes?
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.update(request.getUsername(), request.getPassword()));
    }
}
