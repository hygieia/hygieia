package com.capitalone.dashboard.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.auth.access.Admin;
import com.capitalone.dashboard.model.UserInfo;
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
    
    @RequestMapping(path = "/users/addAdmin", method = RequestMethod.POST)
    public ResponseEntity<UserInfo> addAdmin(@RequestBody UserInfo user) {
        UserInfo savedUser = userInfoService.promoteToAdmin(user.getUsername(), user.getAuthType());

        return new ResponseEntity<UserInfo>(savedUser, HttpStatus.OK);
    }
    
    @RequestMapping(path = "/users/removeAdmin", method = RequestMethod.POST)
    public ResponseEntity<UserInfo> removeAuthorityFromUser(@RequestBody UserInfo user) {
        UserInfo savedUser = userInfoService.demoteFromAdmin(user.getUsername(), user.getAuthType());

        return new ResponseEntity<UserInfo>(savedUser, HttpStatus.OK);
    }
}
