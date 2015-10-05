package com.capitalone.dashboard.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.repository.AuthenticationRepository;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public AuthenticationServiceImpl(
            AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public Iterable<Authentication> all() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Authentication get(ObjectId id) {

        Authentication authentication = authenticationRepository.findOne(id);
        System.out.println("[DEBUG] GET authentication " + authentication);
        return authentication;
    }

    @Override
    public String create(String username, String password) {
        Authentication authentication = new Authentication(username, password);
        authenticationRepository.save(authentication);
        return "User is created";

    }

    @Override
    public String update(String username, String password) {
        Authentication authentication = authenticationRepository.findByUsername(username);
        if (null != authentication) {
            authentication.setPassword(password);
            authenticationRepository.save(authentication);
            return "User is updated";
        } else {
            return "User Does not Exist";
        }

    }

    @Override
    public void delete(ObjectId id) {
        Authentication authentication = authenticationRepository.findOne(id);
        authenticationRepository.delete(authentication);

    }

    @Override
    public void delete(String username) {
        Authentication authentication = authenticationRepository
                .findByUsername(username);
        authenticationRepository.delete(authentication);

    }

    @Override
    public boolean authenticate(String username, String password) {
        boolean flag = false;
        Authentication authentication = authenticationRepository.findByUsername(username);
        if (authentication.getUsername().equals(username) && authentication.getPassword().equals(password)) {
        	flag = true;
        }
        return flag;
    }
}
