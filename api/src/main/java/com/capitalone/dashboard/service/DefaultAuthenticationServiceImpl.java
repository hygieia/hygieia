package com.capitalone.dashboard.service;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.repository.AuthenticationRepository;
import com.google.common.collect.Sets;

@Service
public class DefaultAuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public DefaultAuthenticationServiceImpl(
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
        try {
            authenticationRepository.save(authentication);
            return "User is created";
        } catch (DuplicateKeyException e) {
            return "User already exists";
        }

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
        if (authentication != null) {
            authenticationRepository.delete(authentication);
        }
    }

    @Override
    public void delete(String username) {
        Authentication authentication = authenticationRepository
                .findByUsername(username);
        if (authentication != null) {
            authenticationRepository.delete(authentication);
        }
    }

    @Override
    public org.springframework.security.core.Authentication authenticate(String username, String password) {
        Authentication authentication = authenticationRepository.findByUsername(username);

        if (authentication != null && authentication.checkPassword(password)) {
        	org.springframework.security.core.Authentication user = new PreAuthenticatedAuthenticationToken(authentication.getUsername(), null, getAuthorities(authentication));
            return user;
        }
        return null;
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(Authentication authentication) {
    	//TODO: make enum for authorities
    	//Make role based instead of just looking at the name
    	Collection<GrantedAuthority> authorities = Sets.newHashSet();
    	if("admin".equals(authentication.getUsername())) {
    		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
    		authorities.add(authority);
    	}
    	
    	return authorities;
    }

}
