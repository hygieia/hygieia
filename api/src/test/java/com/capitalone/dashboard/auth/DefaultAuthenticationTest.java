package com.capitalone.dashboard.auth;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.capitalone.dashboard.config.TestAuthConfig;
import com.capitalone.dashboard.service.AuthenticationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.config.WebSecurityConfig;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.repository.AuthenticationRepository;
import com.capitalone.dashboard.repository.UserInfoRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestAuthConfig.class, WebMVCConfig.class, WebSecurityConfig.class})
@WebAppConfiguration
@Rollback(true)
public class DefaultAuthenticationTest {

    @Autowired
    private WebApplicationContext context;


    @Autowired
    private AuthenticationRepository authenticationTestRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AuthenticationService authenticationTestService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void registerUser() throws Exception {


        try {
            when(authenticationTestService.create("somebody","somebody")).thenReturn(AuthenticationFixture.getAuthentication("somebody"));
            when(authenticationTestRepository.save(isA(Authentication.class))).thenReturn(new Authentication("somebody", "somebody"));
            when(userInfoRepository.findByUsernameAndAuthType(isA(String.class), isA(AuthType.class))).thenReturn(new UserInfo());
            mockMvc.perform(post("/registerUser")
                    .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"username\":\"somebody\",\"password\":\"somebody\"}")
            ).andExpect(status().isOk());
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }

    }

}