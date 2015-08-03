package com.capitalone.dashboard.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.request.AuthenticationRequest;
import com.capitalone.dashboard.service.AuthenticationService;


@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @RequestMapping(value = "/authenticateUser", method = POST, consumes = JSON, produces = JSON)
    public ResponseEntity<Boolean> authenticateUser(@Valid @RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authenticationService.authenticate(request.getUsername(), request.getPassword()));
        } catch (org.springframework.dao.DuplicateKeyException de) {
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }
    }

    @RequestMapping(value = "/registerUser", method = POST, consumes = JSON, produces = JSON)
    public ResponseEntity<String> registerUser(@Valid @RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(authenticationService.create(request.getUsername(), request.getPassword()));
        } catch (org.springframework.dao.DuplicateKeyException de) {
            return ResponseEntity.status(HttpStatus.OK).body("User already Exist");
        }
    }

    @RequestMapping(value = "/updateUser", method = POST, consumes = JSON, produces = JSON)
    public ResponseEntity<String> updateUser(@Valid @RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(authenticationService.update(request.getUsername(), request.getPassword()));
        } catch (org.springframework.dao.DuplicateKeyException de) {
            return ResponseEntity.status(HttpStatus.OK).body("User Does Not Exist, Please choose another username");
        }
    }
}
