package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.service.FeatureService;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, WebMVCConfig.class })
@WebAppConfiguration
public class FeatureControllerTest {
	private static Feature mockV1Feature;
	private static Feature mockJiraFeature;
	private static Feature mockJiraFeature2;
	private static Component mockComponent;
	private static Collector mockV1Collector;
	private static Collector mockJiraCollector;
	private static CollectorItem mockItem;
	private static CollectorItem mockItem2;
	private static CollectorItem mockItem3;
	private static final String generalUseDate = "2015-11-01T00:00:00Z";
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static Calendar cal = Calendar.getInstance();
	private static final String maxDateWinner = df.format(new Date());
	private static String maxDateLoser = new String();
	private static String currentSprintEndDate = new String();
	private static final ObjectId jiraCollectorId = new ObjectId();
	private static final ObjectId jiraCollectorId2 = new ObjectId();
	private static final ObjectId v1CollectorId = new ObjectId();
	private static final ObjectId mockComponentId = new ObjectId();
	private static final String KANBAN_START_DATE = "1900-01-01T00:00:00.00Z";
	private static final String KANBAN_END_DATE = "9999-12-31T59:59:59.99Z";
	private static final String KANBAN_SPRINT_ID = "KANBAN";

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private FeatureService featureService;

	@Before
	public void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

		// Date-time modifications
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, -1);
		maxDateLoser = df.format(cal.getTime());
		cal.add(Calendar.DAY_OF_YEAR, +13);
		currentSprintEndDate = df.format(cal.getTime());

		// Helper mock data
		List<String> sOwnerNames = new ArrayList<String>();
		sOwnerNames.add("Goku");
		sOwnerNames.add("Gohan");
		sOwnerNames.add("Picolo");
		List<String> sOwnerDates = new ArrayList<String>();
		sOwnerNames.add(generalUseDate);
		sOwnerNames.add(generalUseDate);
		sOwnerNames.add(generalUseDate);
		List<String> sOwnerStates = new ArrayList<String>();
		sOwnerNames.add("Active");
		sOwnerNames.add("Active");
		sOwnerNames.add("Deleted");
		List<String> sOwnerIds = new ArrayList<String>();
		sOwnerNames.add("9001");
		sOwnerNames.add("8999");
		sOwnerNames.add("7999");
		List<String> sOwnerBools = new ArrayList<String>();
		sOwnerNames.add("True");
		sOwnerNames.add("False");
		sOwnerNames.add("True");

		// VersionOne Mock Feature
		mockV1Feature = new Feature();
		mockV1Feature.setCollectorId(v1CollectorId);
		mockV1Feature.setIsDeleted("True");
		mockV1Feature.setChangeDate(generalUseDate);
		mockV1Feature.setsEpicAssetState("Active");
		mockV1Feature.setsEpicBeginDate(generalUseDate);
		mockV1Feature.setsEpicChangeDate(generalUseDate);
		mockV1Feature.setsEpicEndDate(generalUseDate);
		mockV1Feature.setsEpicID("E-12345");
		mockV1Feature.setsEpicIsDeleted("False");
		mockV1Feature.setsEpicName("Test Epic 1");
		mockV1Feature.setsEpicNumber("12938715");
		mockV1Feature.setsEpicType("Portfolio Feature");
		mockV1Feature.setsEstimate("5");
		mockV1Feature.setsId("B-12345");
		mockV1Feature.setsName("Test Story 1");
		mockV1Feature.setsNumber("12345416");
		mockV1Feature.setsOwnersChangeDate(sOwnerDates);
		mockV1Feature.setsOwnersFullName(sOwnerNames);
		mockV1Feature.setsOwnersID(sOwnerIds);
		mockV1Feature.setsOwnersIsDeleted(sOwnerBools);
		mockV1Feature.setsOwnersShortName(sOwnerNames);
		mockV1Feature.setsOwnersState(sOwnerStates);
		mockV1Feature.setsOwnersUsername(sOwnerNames);
		mockV1Feature.setsProjectBeginDate(generalUseDate);
		mockV1Feature.setsProjectChangeDate(generalUseDate);
		mockV1Feature.setsProjectEndDate(generalUseDate);
		mockV1Feature.setsProjectID("Scope:231870");
		mockV1Feature.setsProjectIsDeleted("False");
		mockV1Feature.setsProjectName("Test Scope 1");
		mockV1Feature.setsProjectPath("Top -> Middle -> Bottome -> "
				+ mockV1Feature.getsProjectName());
		mockV1Feature.setsProjectState("Active");
		mockV1Feature.setsSprintAssetState("Inactive");
		mockV1Feature.setsSprintBeginDate(generalUseDate);
		mockV1Feature.setsSprintChangeDate(generalUseDate);
		mockV1Feature.setsSprintEndDate(maxDateWinner);
		mockV1Feature.setsSprintID("Timebox:12781205");
		mockV1Feature.setsSprintIsDeleted("False");
		mockV1Feature.setsSprintName("Test Sprint 1");
		mockV1Feature.setsState("Inactive");
		mockV1Feature.setsStatus("Accepted");
		mockV1Feature.setsTeamAssetState("Active");
		mockV1Feature.setsTeamChangeDate(generalUseDate);
		mockV1Feature.setsTeamID("Team:124127");
		mockV1Feature.setsTeamIsDeleted("False");
		mockV1Feature.setsTeamName("Protectors of Earth");

		// Jira Mock Feature
		// Mock feature 1
		mockJiraFeature = new Feature();
		mockJiraFeature.setCollectorId(jiraCollectorId);
		mockJiraFeature.setIsDeleted("False");
		mockJiraFeature.setChangeDate(maxDateWinner);
		mockJiraFeature.setsEpicAssetState("Active");
		mockJiraFeature.setsEpicBeginDate("");
		mockJiraFeature.setsEpicChangeDate(maxDateWinner);
		mockJiraFeature.setsEpicEndDate("");
		mockJiraFeature.setsEpicID("32112345");
		mockJiraFeature.setsEpicIsDeleted("");
		mockJiraFeature.setsEpicName("Test Epic 1");
		mockJiraFeature.setsEpicNumber("12938715");
		mockJiraFeature.setsEpicType("");
		mockJiraFeature.setsEstimate("40");
		mockJiraFeature.setsId("0812345");
		mockJiraFeature.setsName("Test Story 2");
		mockJiraFeature.setsNumber("12345416");
		mockJiraFeature.setsOwnersChangeDate(sOwnerDates);
		mockJiraFeature.setsOwnersFullName(sOwnerNames);
		mockJiraFeature.setsOwnersID(sOwnerIds);
		mockJiraFeature.setsOwnersIsDeleted(sOwnerBools);
		mockJiraFeature.setsOwnersShortName(sOwnerNames);
		mockJiraFeature.setsOwnersState(sOwnerStates);
		mockJiraFeature.setsOwnersUsername(sOwnerNames);
		mockJiraFeature.setsProjectBeginDate(maxDateWinner);
		mockJiraFeature.setsProjectChangeDate(maxDateWinner);
		mockJiraFeature.setsProjectEndDate(maxDateWinner);
		mockJiraFeature.setsProjectID("583482");
		mockJiraFeature.setsProjectIsDeleted("False");
		mockJiraFeature.setsProjectName("Saiya-jin Warriors");
		mockJiraFeature.setsProjectPath("");
		mockJiraFeature.setsProjectState("Active");
		mockJiraFeature.setsSprintAssetState("Active");
		mockJiraFeature.setsSprintBeginDate(KANBAN_START_DATE);
		mockJiraFeature.setsSprintChangeDate("");
		mockJiraFeature.setsSprintEndDate(KANBAN_END_DATE);
		mockJiraFeature.setsSprintID(KANBAN_SPRINT_ID);
		mockJiraFeature.setsSprintIsDeleted("False");
		mockJiraFeature.setsSprintName(KANBAN_SPRINT_ID);
		mockJiraFeature.setsState("Active");
		mockJiraFeature.setsStatus("In Progress");
		mockJiraFeature.setsTeamAssetState("Active");
		mockJiraFeature.setsTeamChangeDate(maxDateWinner);
		mockJiraFeature.setsTeamID("08374321");
		mockJiraFeature.setsTeamIsDeleted("False");
		mockJiraFeature.setsTeamName("Saiya-jin Warriors");

		// Mock feature 2
		mockJiraFeature2 = new Feature();
		mockJiraFeature2.setCollectorId(jiraCollectorId2);
		mockJiraFeature2.setIsDeleted("False");
		mockJiraFeature2.setChangeDate(maxDateLoser);
		mockJiraFeature2.setsEpicAssetState("Active");
		mockJiraFeature2.setsEpicBeginDate("");
		mockJiraFeature2.setsEpicChangeDate(maxDateLoser);
		mockJiraFeature2.setsEpicEndDate("");
		mockJiraFeature2.setsEpicID("32112345");
		mockJiraFeature2.setsEpicIsDeleted("");
		mockJiraFeature2.setsEpicName("Test Epic 1");
		mockJiraFeature2.setsEpicNumber("12938715");
		mockJiraFeature2.setsEpicType("");
		mockJiraFeature2.setsEstimate("40");
		mockJiraFeature2.setsId("0812346");
		mockJiraFeature2.setsName("Test Story 3");
		mockJiraFeature2.setsNumber("12345417");
		mockJiraFeature2.setsOwnersChangeDate(sOwnerDates);
		mockJiraFeature2.setsOwnersFullName(sOwnerNames);
		mockJiraFeature2.setsOwnersID(sOwnerIds);
		mockJiraFeature2.setsOwnersIsDeleted(sOwnerBools);
		mockJiraFeature2.setsOwnersShortName(sOwnerNames);
		mockJiraFeature2.setsOwnersState(sOwnerStates);
		mockJiraFeature2.setsOwnersUsername(sOwnerNames);
		mockJiraFeature2.setsProjectBeginDate(maxDateLoser);
		mockJiraFeature2.setsProjectChangeDate(maxDateLoser);
		mockJiraFeature2.setsProjectEndDate(maxDateLoser);
		mockJiraFeature2.setsProjectID("583483");
		mockJiraFeature2.setsProjectIsDeleted("False");
		mockJiraFeature2.setsProjectName("Not Cell!");
		mockJiraFeature2.setsProjectPath("");
		mockJiraFeature2.setsProjectState("Active");
		mockJiraFeature2.setsSprintAssetState("Active");
		mockJiraFeature2.setsSprintBeginDate(maxDateLoser);
		mockJiraFeature2.setsSprintChangeDate(maxDateWinner);
		mockJiraFeature2.setsSprintEndDate(currentSprintEndDate);
		mockJiraFeature2.setsSprintID("1232512");
		mockJiraFeature2.setsSprintIsDeleted("False");
		mockJiraFeature2.setsSprintName("Test Sprint 3");
		mockJiraFeature2.setsState("Active");
		mockJiraFeature2.setsStatus("In Progress");
		mockJiraFeature2.setsTeamAssetState("Active");
		mockJiraFeature2.setsTeamChangeDate(maxDateWinner);
		mockJiraFeature2.setsTeamID("108374321");
		mockJiraFeature2.setsTeamIsDeleted("False");
		mockJiraFeature2.setsTeamName("Saiya-jin Warriors");

		// Creating Collector and Component relationship artifacts
		mockV1Collector = new Collector();
		mockV1Collector.setCollectorType(CollectorType.Feature);
		mockV1Collector.setEnabled(true);
		mockV1Collector.setName("VersionOne Collector");
		mockV1Collector.setOnline(true);
		mockV1Collector.setId(v1CollectorId);

		mockJiraCollector = new Collector();
		mockJiraCollector.setCollectorType(CollectorType.Feature);
		mockJiraCollector.setEnabled(true);
		mockJiraCollector.setName("Jira Collector");
		mockJiraCollector.setOnline(true);
		mockJiraCollector.setId(jiraCollectorId);

		mockItem = new CollectorItem();
		mockItem.setId(new ObjectId());
		mockItem.setCollectorId(v1CollectorId);
		mockItem.setDescription(mockV1Feature.getsTeamName());
		mockItem.setEnabled(true);
		mockItem.setCollector(mockV1Collector);

		mockItem2 = new CollectorItem();
		mockItem2.setId(new ObjectId());
		mockItem2.setCollectorId(jiraCollectorId);
		mockItem2.setDescription(mockJiraFeature.getsTeamName());
		mockItem2.setEnabled(true);
		mockItem2.setCollector(mockJiraCollector);

		mockItem3 = new CollectorItem();
		mockItem3.setId(new ObjectId());
		mockItem3.setCollectorId(jiraCollectorId);
		mockItem3.setDescription(mockJiraFeature2.getsTeamName());
		mockItem3.setEnabled(true);
		mockItem3.setCollector(mockJiraCollector);

		mockComponent = new Component();
		mockComponent.addCollectorItem(CollectorType.Feature, mockItem);
		mockComponent.addCollectorItem(CollectorType.Feature, mockItem2);
		mockComponent.addCollectorItem(CollectorType.Feature, mockItem3);
		mockComponent.setId(mockComponentId);
		mockComponent.setName("Feature Widget Test");
		mockComponent.setOwner("kfk884");
	}

	@After
	public void after() {
		mockV1Feature = null;
		mockJiraFeature = null;
		mockJiraFeature2 = null;
		mockV1Collector = null;
		mockItem = null;
		mockComponent = null;
		mockMvc = null;
	}

	@Test
	public void testRelevantStories_HappyPath() throws Exception {
		String testTeamId = mockV1Feature.getsTeamID();
		List<Feature> features = new ArrayList<Feature>();
		features.add(mockV1Feature);
		features.add(mockJiraFeature);
		features.add(mockJiraFeature2);
		DataResponse<List<Feature>> response = new DataResponse<>(features,
				mockV1Collector.getLastExecuted());

		when(featureService.getFeatureEpicEstimates(mockComponentId, testTeamId, Optional.empty(), Optional.empty())).thenReturn(response);
		mockMvc.perform(get("/feature/" + testTeamId + "?component=" + mockComponentId.toString()))
				.andExpect(status().isOk());
	}

	@Test
	public void testFeatureEstimates_HappyPath() throws Exception {
		String testTeamId = mockV1Feature.getsTeamID();
		List<Feature> features = new ArrayList<Feature>();
		features.add(mockV1Feature);
		features.add(mockJiraFeature);
		features.add(mockJiraFeature2);
		DataResponse<List<Feature>> response = new DataResponse<>(features,
				mockV1Collector.getLastExecuted());

		when(featureService.getFeatureEpicEstimates(mockComponentId, testTeamId, Optional.empty(), Optional.empty())).thenReturn(response);
		mockMvc.perform(
				get("/feature/estimates/super/" + testTeamId + "?component="
						+ mockComponentId.toString())).andExpect(status().isOk());
	}

	@Test
	public void testFeatureEstimates_SameEpicWithEstimates_UniqueResponse() throws Exception {
		String testTeamId = mockV1Feature.getsTeamID();
		List<Feature> features = new ArrayList<Feature>();
		features.add(mockV1Feature);
		features.add(mockJiraFeature);
		features.add(mockJiraFeature2);
		DataResponse<List<Feature>> response = new DataResponse<>(features,
				mockV1Collector.getLastExecuted());

		when(featureService.getFeatureEpicEstimates(mockComponentId, testTeamId, Optional.empty(), Optional.empty())).thenReturn(response);
		mockMvc.perform(
				get("/feature/estimates/super/" + testTeamId + "?component="
						+ mockComponentId.toString()))
				.andExpect(jsonPath("$result[0].sEpicNumber", is(mockV1Feature.getsEpicNumber())))
				.andExpect(jsonPath("$result[0].sEstimate", is(mockV1Feature.getsEstimate())))
				.andExpect(jsonPath("$result", hasSize(3)));
	}
}
