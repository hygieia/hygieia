package com.capitalone.dashboard.auth;
 import static com.capitalone.dashboard.fixture.DashboardFixture.makeDashboard;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 import com.capitalone.dashboard.model.*;
 import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 import com.capitalone.dashboard.repository.AuthenticationRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.capitalone.dashboard.service.DashboardService;
import com.google.common.collect.Lists;
 
 @RunWith(SpringJUnit4ClassRunner.class)
 @SpringApplicationConfiguration(classes = {TestDefaultAuthConfig.class, WebMVCConfig.class, WebSecurityConfig.class})
 @WebAppConfiguration
 @TestPropertySource(locations="classpath:test.properties")
 @Rollback(true)
 public class DefaultSecurityTest {
 
 	@Autowired
     private WebApplicationContext context;
     
 	@Autowired
 	private DashboardService dashboardTestService;
 	
 	@Autowired
 	private DashboardRepository dashboardTestRepository;
 	
 	@Autowired
 	private AuthenticationRepository authenticationTestRepository;
 	
 	@Autowired
 	private UserInfoRepository userInfoRepository;
 
     private MockMvc mockMvc;
     
     @Before
     public void setUp() {
     	mockMvc = MockMvcBuilders
                 .webAppContextSetup(context)
                 .apply(springSecurity())
                 .build();
     }
 
     @Test
     public void appinfo() throws Exception {
         mockMvc.perform(get("/appinfo")).andExpect(status().isOk());
     }

     @Test
     public void viewDashboards() throws Exception {
         mockMvc.perform(get("/dashboard")).andExpect(status().isOk());
     }
     
 
     @Test
     public void createDashboard() throws Exception {
         mockMvc.perform(post("/dashboard")).andExpect(status().isUnauthorized());
     }
     
     @Test
     public void adminUser_deleteDashboard() throws Exception{
     	String jwtHeader = authenticateAs("someAdmin", "someAdminPassword", UserRole.ROLE_ADMIN, UserRole.ROLE_USER);
     	doNothing().when(dashboardTestService).delete(isA(ObjectId.class));
     	mockMvc.perform(delete("/dashboard/54b982620364c80a6136c9f2")
     			.header("AUTHORIZATION", "Bearer " + jwtHeader)
     			).andExpect(status().isNoContent());
     }
 
     @Test
     public void owner_deleteDashboard() throws Exception{
     	String jwtHeader = authenticateAs("someUser", "someUserPassword", UserRole.ROLE_USER);
     	Dashboard dashboard = makeDashboard("t1", "title", "app", "comp","someUser", DashboardType.Team, "ASVTEST", "BAPTEST");
     	String stringObjectId = "54b982620364c80a6136c9f2";
     	ObjectId objectId = new ObjectId(stringObjectId);
     	when(dashboardTestRepository.findOne(objectId)).thenReturn(dashboard);
     	
     	doNothing().when(dashboardTestService).delete(isA(ObjectId.class));
     	mockMvc.perform(delete("/dashboard/"+ stringObjectId)
     			.header("AUTHORIZATION", "Bearer " + jwtHeader)
     			).andExpect(status().isNoContent());
     }
     
     @Test
     public void login() throws Exception{
     	Authentication authentication = new Authentication("someAdmin", "someAdminPassword");
     	when(authenticationTestRepository.findByUsername("someAdmin")).thenReturn(authentication);
     	mockMvc.perform(post("/login")
     			.accept(MediaType.APPLICATION_JSON).param("username", "someAdmin").param("password", "someAdminPassword")
     			).andExpect(status().isOk());
     }
     
     @Test
     public void login_wrongPassword() throws Exception{
     	Authentication authentication = new Authentication("someAdmin", "someAdminPassword");
     	when(authenticationTestRepository.findByUsername("someAdmin")).thenReturn(authentication);
     	mockMvc.perform(post("/login")
     			.accept(MediaType.APPLICATION_JSON).param("username", "someAdmin").param("password", "badPassword")
     			).andExpect(status().isUnauthorized());
     }
     
 	private String authenticateAs(String username, String password, UserRole... roles) throws Exception {
 		Authentication authentication = new Authentication(username, password);
     	when(authenticationTestRepository.findByUsername(username)).thenReturn(authentication);
     	
     	UserInfo userInfo = new UserInfo();
     	userInfo.setAuthorities(Lists.newArrayList(roles));
     	
     	when(userInfoRepository.findByUsernameAndAuthType(username, AuthType.STANDARD)).thenReturn(userInfo);
     	
     	MvcResult loginResult = mockMvc.perform(post("/login")
     			.accept(MediaType.APPLICATION_JSON).param("username", username).param("password", password)
     			).andExpect(status().isOk()).andReturn();
     	
     	return loginResult.getResponse().getHeader(ReflectionTestUtils.getField(TokenAuthenticationServiceImpl.class, "AUTH_RESPONSE_HEADER").toString());
 	}
 }
