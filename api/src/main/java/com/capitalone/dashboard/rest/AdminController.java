package com.capitalone.dashboard.rest;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.auth.access.Admin;
import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.service.AuthenticationService;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@RestController
@RequestMapping("/admin")
@Admin
public class AdminController {
    
    private final AuthenticationService authService;
    
    @Autowired
    public AdminController(AuthenticationService authService) {
        this.authService = authService;
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public Collection<Map<String, Object>> getUsers() {
        Iterable<Authentication> users = authService.all();
        Collection<Map<String, Object>> scrubbedUsers = scrubUsers(users);
        
        return scrubbedUsers;
    }

    @RequestMapping(path = "/users/addAdmin", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addAdmin(@RequestBody Authentication user) {
        Authentication savedUser = authService.promoteToAdmin(user.getUsername());
        
        return new ResponseEntity<Map<String, Object>>(scrubUser(savedUser), HttpStatus.OK);
    }
    
    @RequestMapping(path = "/users/removeAdmin", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> removeAuthorityFromUser(@RequestBody Authentication user) {
        Authentication savedUser = authService.demoteFromAdmin(user.getUsername());

        return new ResponseEntity<Map<String, Object>>(scrubUser(savedUser), HttpStatus.OK);
    }
    
    private Collection<Map<String, Object>> scrubUsers(Iterable<Authentication> users) {
        Collection<Map<String,Object>> scrubbedUsers = Sets.newHashSet();
        users.forEach(user -> {
            scrubbedUsers.add(scrubUser(user));
        });
        return scrubbedUsers;
    }

    private Map<String, Object> scrubUser(Authentication user) {
        Map<String, Object> userMap = Maps.newHashMap();
        userMap.put("username", user.getUsername());
        userMap.put("roles", user.getRoles());
        
        return userMap;
    }
}
