package com.capitalone.dashboard.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.auth.access.Admin;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.service.UserInfoService;

@RestController
@RequestMapping("/admin")
@Admin
public class AdminController {
    
    private final UserInfoService userInfoService;
    
    @Autowired
    public AdminController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public Collection<UserInfo> getUsers() {
        return userInfoService.getUsers();
    }
    
    @RequestMapping(path = "/users/{authType}/{username}/authorities", method = RequestMethod.POST)
    public ResponseEntity<UserInfo> addAuthorityToUser(@PathVariable AuthType authType, @PathVariable String username, @RequestBody UserRole role) {
        UserInfo user = userInfoService.addAuthorityToUser(authType, username, role);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<UserInfo>(user, HttpStatus.CREATED);
    }
    
    @RequestMapping(path = "/users/{authType}/{username}/authorities/{role}", method = RequestMethod.DELETE)
    public ResponseEntity<UserInfo> removeAuthorityFromUser(@PathVariable AuthType authType, @PathVariable String username, @PathVariable UserRole role) {
        UserInfo user = userInfoService.removeAuthorityFromUser(authType, username, role);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<UserInfo>(user, HttpStatus.OK);
    }
}
