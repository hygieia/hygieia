package com.capitalone.dashboard.service;

import java.util.List;

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
        List<Authentication> authenticationList = authenticationRepository.findByUsernameAndPassword(username, password);
        if (authenticationList.size() == 1) {
            if (authenticationList.get(0).getUsername().equals(username) && authenticationList.get(0).getPassword().equals(password)) {
                flag = true;
            }
        }
        return flag;
    }
}
