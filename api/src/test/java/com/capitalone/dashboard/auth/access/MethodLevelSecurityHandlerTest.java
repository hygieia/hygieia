package com.capitalone.dashboard.auth.access;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.capitalone.dashboard.model.*;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;

import com.capitalone.dashboard.auth.AuthenticationFixture;
import com.capitalone.dashboard.auth.access.MethodLevelSecurityHandler;
import com.capitalone.dashboard.repository.DashboardRepository;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MethodLevelSecurityHandlerTest {

	private static final String USERNAME = "username";
	private static final String SOME_OTHER_USER = "someotheruser";
	private static final String configItemAppName = "ASVTEST";
	private static final String configItemComponentName = "BAPTEST";
	
	@InjectMocks
	private MethodLevelSecurityHandler handler;
	
	@Mock
	private DashboardRepository dashboardRepository;
	
	@Before
	public void setup() {
		SecurityContextHolder.clearContext();
	}
	
	@Test
	public void testIsOwnerOfDashboard_noDashFound() {
		when(dashboardRepository.findOne(any(ObjectId.class))).thenReturn(null);
		
		assertFalse(handler.isOwnerOfDashboard(new ObjectId()));
	}
	
	@Test
	public void testIsOwnerOfDashboard_legacyDashFound() {
		initiateSecurityContext();
		List<String> activeWidgets = new ArrayList<>();
		Dashboard dashboard = new Dashboard("team", "title", null, null, DashboardType.Team, configItemAppName,configItemComponentName,activeWidgets, false, ScoreDisplayType.HEADER);
		dashboard.setOwner(USERNAME);
		when(dashboardRepository.findOne(any(ObjectId.class))).thenReturn(dashboard);
		
		assertTrue(handler.isOwnerOfDashboard(new ObjectId()));
	}

	@Test
	public void testIsOwnerOfDashboard_newDashFound() {
		initiateSecurityContext();
		List<String> activeWidgets = new ArrayList<>();
		List<Owner> owners = new ArrayList<>();
		owners.add(new Owner(USERNAME, AuthType.STANDARD));
		Dashboard dashboard = new Dashboard("team", "title", null, owners, DashboardType.Team, configItemAppName,configItemComponentName,activeWidgets, false, ScoreDisplayType.HEADER);
		when(dashboardRepository.findOne(any(ObjectId.class))).thenReturn(dashboard);
		
		assertTrue(handler.isOwnerOfDashboard(new ObjectId()));
	}
	
	@Test
	public void testIsNotOwnerOfDashboard() {
		initiateSecurityContext();
		List<String> activeWidgets = new ArrayList<>();
		Dashboard dashboard = new Dashboard("team", "title", null, null, DashboardType.Team,configItemAppName,configItemComponentName,activeWidgets, false, ScoreDisplayType.HEADER);
		dashboard.setOwner(SOME_OTHER_USER);
		when(dashboardRepository.findOne(any(ObjectId.class))).thenReturn(dashboard);
		
		assertFalse(handler.isOwnerOfDashboard(new ObjectId()));
	}
	
	private void initiateSecurityContext() {
		AuthenticationFixture.createAuthentication(USERNAME);
	}
}
