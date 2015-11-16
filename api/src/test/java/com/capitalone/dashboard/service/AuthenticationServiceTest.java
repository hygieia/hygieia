package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.repository.AuthenticationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
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
}
