package com.capitalone.dashboard.auth;

import static com.capitalone.dashboard.fixture.DashboardFixture.makeDashboard;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.capitalone.dashboard.auth.token.TokenAuthenticationService;
import com.capitalone.dashboard.config.TestAuthConfig;
import com.capitalone.dashboard.config.TestConfig;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.capitalone.dashboard.auth.token.TokenAuthenticationServiceImpl;
import com.capitalone.dashboard.config.TestDefaultAuthConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.config.WebSecurityConfig;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.AuthenticationRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.capitalone.dashboard.service.DashboardService;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestAuthConfig.class, WebMVCConfig.class, WebSecurityConfig.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test.properties")
@Rollback(true)
public class DefaultAuthenticationTest {

    @Autowired
    private WebApplicationContext context;


    @Autowired
    private AuthenticationRepository authenticationTestRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;


    @Autowired
    private TokenAuthenticationServiceImpl tokenAuthenticationTestServiceImpl;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        tokenAuthenticationTestServiceImpl = Mockito.mock(TokenAuthenticationServiceImpl.class);
    }

    @Test
    public void registerUser() throws Exception {
        when(authenticationTestRepository.save(isA(Authentication.class))).thenReturn(new Authentication("somebody", "somebody"));
        when(userInfoRepository.findByUsernameAndAuthType(isA(String.class), isA(AuthType.class))).thenReturn(new UserInfo());
        doNothing().when(tokenAuthenticationTestServiceImpl).addAuthentication(isA(HttpServletResponse.class), isA(org.springframework.security.core.Authentication.class));
        mockMvc.perform(post("/registerUser")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"username\":\"somebody\",\"password\":\"somebody\"}")
        ).andExpect(status().isOk());

    }

}