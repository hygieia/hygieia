package com.capitalone.dashboard.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class UserInfoServiceImplTest {
    
    @Mock
    private UserInfoRepository userInfoRepository;
    
    @InjectMocks
    private UserInfoServiceImpl service;

    @Test
    public void shouldGetAuthorities() {
        UserInfo user = new UserInfo();
        user.setUsername("user");
        user.setAuthType(AuthType.STANDARD);
        user.setAuthorities(Lists.newArrayList(UserRole.ROLE_ADMIN));
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        when(userInfoRepository.findByUsernameAndAuthType("user", AuthType.STANDARD)).thenReturn(user);
        Collection<? extends GrantedAuthority> authorities = service.getAuthorities("user", AuthType.STANDARD);
        
        assertTrue(authorities.contains(authority));
    }

}
