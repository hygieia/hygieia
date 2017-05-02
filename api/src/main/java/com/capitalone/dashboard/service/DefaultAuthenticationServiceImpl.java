package com.capitalone.dashboard.service;

import java.util.Collection;
import java.util.HashSet;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.auth.exceptions.DeleteLastAdminException;
import com.capitalone.dashboard.auth.exceptions.UserNotFoundException;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.AuthenticationRepository;

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
        return authenticationRepository.findAll();
    }

    @Override
    public Authentication get(ObjectId id) {

        Authentication authentication = authenticationRepository.findOne(id);
        return authentication;
    }

    @Override
    public org.springframework.security.core.Authentication create(String username, String password) {
        Authentication user = new Authentication(username, password);
        
        user.getRoles().add(UserRole.ROLE_USER);
        
        //TODO: Remove when there is a better solution for admin user.
        if(user.getUsername().equals("admin")) user.getRoles().add(UserRole.ROLE_ADMIN);
        
        Authentication authentication = authenticationRepository.save(user);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authentication.getUsername(), authentication.getPassword(), convertRolesToAuthorities(authentication.getRoles()));
        token.setDetails(AuthType.STANDARD);
        return token;
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

        //TODO: Remove when better solution for first admin
        if(authentication.getUsername().equals("admin")) authentication.getRoles().add(UserRole.ROLE_ADMIN);
        
        if (authentication != null && authentication.checkPassword(password)) {
        	Collection<GrantedAuthority> authorities = convertRolesToAuthorities(authentication.getRoles());
        	
            return new UsernamePasswordAuthenticationToken(authentication.getUsername(), authentication.getPassword(), authorities);
        }

        throw new BadCredentialsException("Login Failed: Invalid credentials for user " + username);
    }
    
    @Override
    public Authentication addRole(String username, UserRole userRole) {
        Authentication user = authenticationRepository.findByUsername(username);

        if (user == null) { throw new UserNotFoundException(username, AuthType.STANDARD); }

        user.getRoles().add(userRole);
        Authentication savedUser = authenticationRepository.save(user);
        
        return savedUser;
    }
    
    @Override
    public Authentication removeRole(String username, UserRole userRole) {
        Authentication user = authenticationRepository.findByUsername(username);
        if (user == null) { 
            throw new UserNotFoundException(username, AuthType.STANDARD); 
        }
        
        if (UserRole.ROLE_ADMIN.equals(userRole)) {
            checkLastAdmin();
        }
        
        user.getRoles().remove(userRole);
        Authentication savedUser = authenticationRepository.save(user);
        return savedUser;
    }

    private void checkLastAdmin() {
        int numberOfAdmins = authenticationRepository.findByRolesIn(UserRole.ROLE_ADMIN).size();
        if (numberOfAdmins <= 1) { 
            throw new DeleteLastAdminException(); 
        }
    }

    private Collection<GrantedAuthority> convertRolesToAuthorities(Collection<UserRole> roles) {
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        });
        return authorities;
    }
    
}
