package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.repository.AuthenticationRepository;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock AuthenticationRepository authRepo;
    @InjectMocks AuthenticationServiceImpl authService;

    @Test
    public void testOldPwAuthentication() throws Exception {
        final String pw = "pass1";

        Authentication nonHashPass = new Authentication("u1", pw);
        Field pwField = nonHashPass.getClass().getDeclaredField("password");
        pwField.setAccessible(true);
        pwField.set(nonHashPass, pw);

        when(authRepo.findByUsername(anyString())).thenReturn(nonHashPass);
        assertTrue(authService.authenticate("u1", "pass1"));
    }

    @Test
    public void testHashedPwAuthentication() throws Exception {
        final String pw = "pass1";

        Authentication auth = new Authentication("u1", pw);

        when(authRepo.findByUsername(anyString())).thenReturn(auth);
        assertTrue(authService.authenticate("u1", "pass1"));
    }

    @Test
    public void testCreate() throws Exception {
        String res = "User is created";
        Authentication authentication = new Authentication("u1", "p1");
        when (authRepo.save(authentication)).thenReturn(authentication);
        assertEquals(authService.create(authentication.getUsername(), authentication.getPassword()), res);
        verify(authRepo, times(1)).save(any(Authentication.class));
    }

    @Test
    public void testCreateExists() throws Exception {
        String res = "User already exists";
        Authentication authentication = new Authentication("u1", "p1");
        when(authRepo.save(any(Authentication.class))).thenThrow(DuplicateKeyException.class);
        assertEquals(authService.create(authentication.getUsername(), authentication.getPassword()), res);
        verify(authRepo, times(0)).save(authentication);
    }
    @Test
    public void testUpdate() throws Exception {
        String res = "User is updated";
        Authentication authentication = new Authentication("u1", "p1");
        when (authRepo.findByUsername(authentication.getUsername())).thenReturn(authentication);
        assertEquals(authService.update(authentication.getUsername(), authentication.getPassword()), res);
        verify(authRepo, times(1)).save(authentication);
    }

    @Test
    public void testUpdateNoExist() throws Exception {
        String res = "User Does not Exist";
        Authentication authentication = new Authentication("u1", "p1");
        when (authRepo.findByUsername(authentication.getUsername())).thenReturn(null);
        assertEquals(authService.update(authentication.getUsername(), authentication.getPassword()), res);
        verify(authRepo, times(0)).save(authentication);
    }

    @Test
    public void testDelete() throws Exception {
        Authentication authentication = new Authentication("u1", "p1");
        when (authRepo.findByUsername(authentication.getUsername())).thenReturn(authentication);
        authService.delete(authentication.getUsername());
        verify(authRepo, times(1)).delete(authentication);
    }

    @Test
    public void testDeleteByKey() throws Exception {
        Authentication authentication = new Authentication("u1", "p1");
        ObjectId obj = new ObjectId();
        when (authRepo.findOne(any(ObjectId.class))).thenReturn(authentication);
        authService.delete(obj);
        verify(authRepo, times(1)).delete(authentication);
    }

    @Test
    public void testDeleteByKeyExists() throws Exception {
        Authentication authentication = new Authentication("u1", "p1");
        ObjectId obj = new ObjectId();
        when (authRepo.findOne(any(ObjectId.class))).thenReturn(null);
        authService.delete(obj);
        verify(authRepo, times(0)).delete(authentication);
    }

    public void testAuthenticate() throws Exception {

    }
}
