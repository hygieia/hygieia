package com.capitalone.dashboard.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.auth.exceptions.DeleteLastAdminException;
import com.capitalone.dashboard.auth.exceptions.UserNotFoundException;
import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.AuthenticationRepository;
import com.google.common.collect.Lists;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock 
    private AuthenticationRepository authRepo;
    
    @InjectMocks 
    private DefaultAuthenticationServiceImpl authService;

    @Test
    public void testOldPwAuthentication() throws Exception {
        final String pw = "pass1";

        Authentication nonHashPass = new Authentication("u1", pw);
        Field pwField = nonHashPass.getClass().getDeclaredField("password");
        pwField.setAccessible(true);
        pwField.set(nonHashPass, pw);

        when(authRepo.findByUsername(anyString())).thenReturn(nonHashPass);
        assertNotNull(authService.authenticate("u1", "pass1"));
    }

    @Test
    public void testHashedPwAuthentication() throws Exception {
        final String pw = "pass1";

        Authentication auth = new Authentication("u1", pw);

        when(authRepo.findByUsername(anyString())).thenReturn(auth);
        assertNotNull(authService.authenticate("u1", "pass1"));
    }
    
    @Test(expected=UserNotFoundException.class)
    public void shouldNotPromoteNonExistingUserToAdmin() {
        String username = "user";
        when(authRepo.findByUsername(username)).thenReturn(null);
        
        authService.addRole(username, UserRole.ROLE_ADMIN);
        
        fail("Exception should have been thrown.");
    }
    
    @Test
    public void shouldPromoteExistingUserToAdmin() {
        String username = "user";
        Authentication user = new Authentication(username, "password");
        user.setUsername(username);
        when(authRepo.findByUsername(username)).thenReturn(user);
        when(authRepo.save(isA(Authentication.class))).thenReturn(user);
        Authentication result = authService.addRole(username, UserRole.ROLE_ADMIN);
        
        assertNotNull(result);
        assertTrue(result.getRoles().contains(UserRole.ROLE_ADMIN));
        verify(authRepo).save(user);
    }
    
    @Test(expected=DeleteLastAdminException.class)
    public void shouldNotDeleteLastAdmin() {
        String username = "user";
        Authentication user = new Authentication(username, "password");
        Collection<Authentication> users = new ArrayList<>();
        when(authRepo.findByRolesIn(UserRole.ROLE_ADMIN)).thenReturn(users);
        when(authRepo.findByUsername(username)).thenReturn(user);
        
        authService.removeRole(username, UserRole.ROLE_ADMIN);
        
        fail("Should have thrown an exception");
        
    }
    
    @Test(expected=UserNotFoundException.class)
    public void shouldNotRemoveAdminFromNonExistingUSer() {
        String username = "user";
        Collection<Authentication> users = Lists.newArrayList(new Authentication(username, "password"), new Authentication(username, "password"));
        when(authRepo.findByRolesIn(UserRole.ROLE_ADMIN)).thenReturn(users);
        when(authRepo.findByUsername(username)).thenReturn(null);
        
        authService.removeRole(username, UserRole.ROLE_ADMIN);
        
        fail("Exception should have been thrown.");
    }
    
    @Test
    public void shouldRemoveAdminFromExistingUser() {
        String username = "user";
        Authentication user = new Authentication(username, "password");
        user.getRoles().add(UserRole.ROLE_ADMIN);
        Collection<Authentication> users = Lists.newArrayList(new Authentication(username, "password"), new Authentication(username, "password"));
        when(authRepo.findByRolesIn(UserRole.ROLE_ADMIN)).thenReturn(users);
        when(authRepo.findByUsername(username)).thenReturn(user);
        when(authRepo.save(isA(Authentication.class))).thenReturn(user);
        
        Authentication result = authService.removeRole(username, UserRole.ROLE_ADMIN);
        
        assertNotNull(result);
        assertFalse(result.getRoles().contains(UserRole.ROLE_ADMIN));
        verify(authRepo).save(user);
        
    }
}
