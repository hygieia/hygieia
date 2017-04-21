package com.capitalone.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.repository.UserInfoRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserInfoServiceImplTest {
    
    @Mock
    private UserInfoRepository userInfoRepo;
    
    @InjectMocks
    private UserInfoServiceImpl service;

    @Test
    public void shouldGetUserInfo() {
        String username = "username";
        AuthType authType = AuthType.STANDARD;
        UserInfo user = new UserInfo();
        when(userInfoRepo.findByUsernameAndAuthType(username, authType)).thenReturn(user);
        
        UserInfo result = service.getUserInfo(username, authType);
        
        assertSame(result, user);
    }
    
    @Test
    public void shouldGetUserInfoForNonExistingUser() {
        String username = "username";
        AuthType authType = AuthType.STANDARD;
        UserInfo user = new UserInfo();
        when(userInfoRepo.findByUsernameAndAuthType(username, authType)).thenReturn(null);
        
        UserInfo result = service.getUserInfo(username, authType);
        
        assertNotSame(result, user);
        assertEquals(username, result.getUsername());
        assertEquals(authType, result.getAuthType());
        verify(userInfoRepo).save(any(UserInfo.class));
    }

}
