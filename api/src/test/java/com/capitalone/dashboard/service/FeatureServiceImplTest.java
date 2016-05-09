package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.FeatureRepository;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeatureServiceImplTest {
	private static Feature mockV1Feature;
	private static Feature mockJiraFeature;
	private static Feature mockJiraFeature2;
	private static Feature mockJiraFeature3;
	private static Component mockComponent;
	private static Collector mockV1Collector;
	private static Collector mockJiraCollector;
	private static CollectorItem mockItem;
	private static CollectorItem mockItem2;
	private static CollectorItem mockItem3;
	private static CollectorItem mockItem4;
	private static final String generalUseDate = "2015-11-01T00:00:00.000-00:00";
	private static final String KANBAN_START_DATE = "1900-01-01T00:00:00.000-00:00";
	private static final String KANBAN_END_DATE = "9999-12-31T59:59:59.999-99:99";
	private static final String KANBAN_SPRINT_ID = "KANBAN";
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000-00:00");
	private static Calendar cal = Calendar.getInstance();
	private static final String maxDateWinner = DatatypeConverter
			.printDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
	private static String maxDateLoser = new String();
	private static String currentSprintEndDate = new String();
	private static final ObjectId jiraCollectorId = new ObjectId();
	private static final ObjectId jiraCollectorId2 = new ObjectId();
	private static final ObjectId jiraCollectorId3 = new ObjectId();
	private static final ObjectId v1CollectorId = new ObjectId();
	private static final ObjectId mockComponentId = new ObjectId();

	@Mock
	ComponentRepository componentRepository;
	@Mock
	FeatureRepository featureRepository;
	@Mock
	private CollectorRepository collectorRepository;
	@InjectMocks
	FeatureServiceImpl featureService;

	@Before
	public void setup() {
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
		mockV1Feature
				.setsProjectPath("Top -> Middle -> Bottome -> " + mockV1Feature.getsProjectName());
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
		mockJiraFeature.setsSprintBeginDate(maxDateLoser);
		mockJiraFeature.setsSprintChangeDate(maxDateWinner);
		mockJiraFeature.setsSprintEndDate(currentSprintEndDate);
		mockJiraFeature.setsSprintID("1232512");
		mockJiraFeature.setsSprintIsDeleted("False");
		mockJiraFeature.setsSprintName("Test Sprint 2");
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
		mockJiraFeature2.setsTeamID("08374321");
		mockJiraFeature2.setsTeamIsDeleted("False");
		mockJiraFeature2.setsTeamName("Saiya-jin Warriors");

		// Mock feature 2
		mockJiraFeature3 = new Feature();
		mockJiraFeature3.setCollectorId(jiraCollectorId3);
		mockJiraFeature3.setIsDeleted("False");
		mockJiraFeature3.setChangeDate(maxDateLoser);
		mockJiraFeature3.setsEpicAssetState("Active");
		mockJiraFeature3.setsEpicBeginDate("");
		mockJiraFeature3.setsEpicChangeDate(maxDateLoser);
		mockJiraFeature3.setsEpicEndDate("");
		mockJiraFeature3.setsEpicID("32112345");
		mockJiraFeature3.setsEpicIsDeleted("");
		mockJiraFeature3.setsEpicName("Test Epic 1");
		mockJiraFeature3.setsEpicNumber("12938715");
		mockJiraFeature3.setsEpicType("");
		mockJiraFeature3.setsEstimate("40");
		mockJiraFeature3.setsId("0812347");
		mockJiraFeature3.setsName("Test Story 4");
		mockJiraFeature3.setsNumber("12345417");
		mockJiraFeature3.setsOwnersChangeDate(sOwnerDates);
		mockJiraFeature3.setsOwnersFullName(sOwnerNames);
		mockJiraFeature3.setsOwnersID(sOwnerIds);
		mockJiraFeature3.setsOwnersIsDeleted(sOwnerBools);
		mockJiraFeature3.setsOwnersShortName(sOwnerNames);
		mockJiraFeature3.setsOwnersState(sOwnerStates);
		mockJiraFeature3.setsOwnersUsername(sOwnerNames);
		mockJiraFeature3.setsProjectBeginDate(maxDateLoser);
		mockJiraFeature3.setsProjectChangeDate(maxDateLoser);
		mockJiraFeature3.setsProjectEndDate(maxDateLoser);
		mockJiraFeature3.setsProjectID("583483");
		mockJiraFeature3.setsProjectIsDeleted("False");
		mockJiraFeature3.setsProjectName("Not Cell!");
		mockJiraFeature3.setsProjectPath("");
		mockJiraFeature3.setsProjectState("Active");
		mockJiraFeature3.setsSprintAssetState("Active");
		mockJiraFeature3.setsSprintBeginDate(KANBAN_START_DATE);
		mockJiraFeature3.setsSprintChangeDate(maxDateWinner);
		mockJiraFeature3.setsSprintEndDate(KANBAN_END_DATE);
		mockJiraFeature3.setsSprintID(KANBAN_SPRINT_ID);
		mockJiraFeature3.setsSprintIsDeleted("False");
		mockJiraFeature3.setsSprintName(KANBAN_SPRINT_ID);
		mockJiraFeature3.setsState("Active");
		mockJiraFeature3.setsStatus("In Progress");
		mockJiraFeature3.setsTeamAssetState("Active");
		mockJiraFeature3.setsTeamChangeDate(maxDateWinner);
		mockJiraFeature3.setsTeamID("08374321");
		mockJiraFeature3.setsTeamIsDeleted("False");
		mockJiraFeature3.setsTeamName("Saiya-jin Warriors");

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

		mockItem4 = new CollectorItem();
		mockItem4.setId(new ObjectId());
		mockItem4.setCollectorId(jiraCollectorId);
		mockItem4.setDescription(mockJiraFeature3.getsTeamName());
		mockItem4.setEnabled(true);
		mockItem4.setCollector(mockJiraCollector);

		mockComponent = new Component();
		mockComponent.getCollectorItems().put(CollectorType.ScopeOwner,
				Arrays.asList(mockItem2, mockItem3, mockItem4));
		mockComponent.setId(mockComponentId);
		mockComponent.setName("Feature Widget Test");
		mockComponent.setOwner("kfk884");

		// Saving to mock repos
		componentRepository.save(mockComponent);
		collectorRepository.save(mockJiraCollector);
		collectorRepository.save(mockV1Collector);
		featureRepository.save(mockV1Feature);
		featureRepository.save(mockJiraFeature);
		featureRepository.save(mockJiraFeature2);
		featureRepository.save(mockJiraFeature3);
	}

	@After
	public void cleanup() {
		mockV1Feature = null;
		mockJiraFeature = null;
		mockJiraFeature2 = null;
		mockJiraFeature3 = null;
		mockV1Collector = null;
		mockItem = null;
		mockItem2 = null;
		mockItem3 = null;
		mockItem4 = null;
		mockComponent = null;
	}

	@Test
	public void testGetFeatureEstimates_ManySameSuperFeatures_OneSuperFeatureRs() {
		when(componentRepository.findOne(mockComponentId)).thenReturn(mockComponent);
		when(collectorRepository.findOne(mockItem2.getCollectorId())).thenReturn(mockJiraCollector);
		when(featureRepository.getInProgressFeaturesEstimatesByTeamId((String) notNull(),
				(String) notNull())).thenReturn(Arrays.asList(mockJiraFeature, mockJiraFeature2));

		DataResponse<List<Feature>> result = featureService.getFeatureEstimates(mockComponentId,
				mockJiraFeature.getsTeamID(), Optional.empty());
		assertThat(
				"There should only be one result even with multiple same super features over several sub features",
				result.getResult(), hasSize(1));
		assertThat(
				"The total super feature estimate should be the sum total of any similar super features present in the response",
				Integer.valueOf(result.getResult().get(0).getsEstimate()), equalTo(80));
	}

	@Test
	public void testGetCurrentSprintDetail_ValidKanbanTeam_ShowKanban() {
		when(componentRepository.findOne(mockComponentId)).thenReturn(mockComponent);
		when(collectorRepository.findOne(mockItem3.getCollectorId())).thenReturn(mockJiraCollector);
		when(featureRepository.getCurrentSprintDetail((String) notNull(), (String) notNull()))
				.thenReturn(Arrays.asList(mockJiraFeature3));

		DataResponse<List<Feature>> result = featureService.getCurrentSprintDetail(mockComponentId,
				mockJiraFeature3.getsTeamID(), Optional.empty());
		assertThat(
				"There should only be one result even with multiple same super features over several sub features",
				result.getResult(), hasSize(1));
		assertThat(
				"The total super feature estimate should be the sum total of any similar super features present in the response",
				result.getResult().get(0).getsSprintName(), equalTo(KANBAN_SPRINT_ID));
	}
}
