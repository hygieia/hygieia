package com.capitalone.dashboard.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.repository.AuthenticationRepository;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock AuthenticationRepository authRepo;
    @InjectMocks DefaultAuthenticationServiceImpl authService;

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
}
