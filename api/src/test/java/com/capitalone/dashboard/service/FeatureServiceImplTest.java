package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.SprintEstimate;
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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeatureServiceImplTest {
	private static Feature mockV1Feature;
	private static Feature mockJiraFeature;
	private static Feature mockJiraFeature2;
	private static Feature mockJiraFeature3_oldkanban;
	private static Feature mockJiraFeature4_sprintkanban;
	private static Feature mockJiraFeature5_nullkanban;
	
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
	private static final String KANBAN_OLD_SPRINT_ID = "KANBAN";
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
	
	private static final List<String> OWNER_NAMES = Arrays.asList("Goku", "Gohan", "Picolo");
	private static final List<String> OWNER_DATES = Arrays.asList(generalUseDate, generalUseDate, generalUseDate);
	private static final List<String> OWNER_STATES = Arrays.asList("Active", "Active", "Deleted");
	private static final List<String> OWNER_IDS = Arrays.asList("9001", "8999", "7999");
	private static final List<String> OWNER_BOOLS = Arrays.asList("True", "False", "True");

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

		
//		(ObjectId collectorId, String sId, String sStatus, String sState, Integer sEstimate, String changeDate, boolean isDeleted, 
//				String teamId, String sTeamChangeDate,
//				String sSprintID, String sSprintChangeDate, String sSprintBeginDate, String sSprintEndDate,
//				String sEpicID, String sEpicChangeDate, String sEpicBeginDate, String sEpicEndDate,
//				String sProjectID, String sProjectChangeDate, String sProjectBeginDate, String sProjectEndDate)
		
		// VersionOne Mock Feature
		mockV1Feature = createFeature(v1CollectorId, "B-12345", "Accepted", "Inactive", 5, generalUseDate, true,
				"Team:124127", generalUseDate,
				"Timebox:12781205", generalUseDate, generalUseDate, maxDateWinner,
				"E-12345", generalUseDate, generalUseDate, generalUseDate,
				"Scope:231870", generalUseDate, generalUseDate, generalUseDate);

		// Jira Mock Feature
		// Mock feature 1
		mockJiraFeature = createFeature(jiraCollectorId, "0812345", "In Progress", "Active", 40, maxDateWinner, false,
				"08374321", maxDateWinner,
				"1232512", maxDateWinner, maxDateLoser, currentSprintEndDate,
				"32112345", maxDateWinner, generalUseDate, generalUseDate,
				"583482", maxDateWinner, maxDateWinner, maxDateWinner);
		
		// Mock feature 2
		mockJiraFeature2 = createFeature(jiraCollectorId2, "0812346", "Done", "Active", 50, maxDateLoser, false,
				"08374321", maxDateWinner,
				"1232512", maxDateWinner, maxDateLoser, currentSprintEndDate,
				"32112345", maxDateLoser, "", "",
				"583483", maxDateLoser, maxDateLoser, maxDateLoser);

		// Mock feature 3
		mockJiraFeature3_oldkanban = createFeature(jiraCollectorId3, "0812347", "Open", "Active", 40, maxDateLoser, false,
				"08374321", maxDateWinner,
				KANBAN_OLD_SPRINT_ID, maxDateWinner, KANBAN_START_DATE, KANBAN_END_DATE,
				"32112345", maxDateLoser, "", "",
				"583483", maxDateLoser, maxDateLoser, maxDateLoser);
		
		mockJiraFeature4_sprintkanban = createFeature(jiraCollectorId3, "0812347", "In Progress", "Active", 50, maxDateLoser, false,
				"08374321", maxDateWinner,
				"11111", maxDateWinner, KANBAN_START_DATE, KANBAN_END_DATE,
				"32112345", maxDateLoser, "", "",
				"583483", maxDateLoser, maxDateLoser, maxDateLoser);
		
		mockJiraFeature5_nullkanban = createFeature(jiraCollectorId3, "0812347", "Done", "Active", 60, maxDateLoser, false,
				"08374321", maxDateWinner,
				"22222", maxDateWinner, "", "",
				"32112345", maxDateLoser, "", "",
				"583483", maxDateLoser, maxDateLoser, maxDateLoser);

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
		mockItem4.setDescription(mockJiraFeature3_oldkanban.getsTeamName());
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
		featureRepository.save(mockJiraFeature3_oldkanban);
	}

	@After
	public void cleanup() {
		mockV1Feature = null;
		mockJiraFeature = null;
		mockJiraFeature2 = null;
		mockJiraFeature3_oldkanban = null;
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
		when(featureRepository.findByActiveEndingSprintsMinimal((String) notNull(),
				(String) notNull())).thenReturn(Arrays.asList(mockJiraFeature, mockJiraFeature2));

		DataResponse<List<Feature>> result = featureService.getFeatureEpicEstimates(mockComponentId,
				mockJiraFeature.getsTeamID(), Optional.empty(), Optional.empty());
		assertThat(
				"There should only be one result even with multiple same super features over several sub features",
				result.getResult(), hasSize(1));
		assertThat(
				"The total super feature estimate should be the sum total of any similar super features present in the response",
				Integer.valueOf(result.getResult().get(0).getsEstimate()), equalTo(90));
	}
	
	@Test
	public void testGetFeatureEstimates_ManySameSuperFeatures_OneSuperFeatureRs_Hours() {
		when(componentRepository.findOne(mockComponentId)).thenReturn(mockComponent);
		when(collectorRepository.findOne(mockItem2.getCollectorId())).thenReturn(mockJiraCollector);
		when(featureRepository.findByActiveEndingSprintsMinimal((String) notNull(),
				(String) notNull())).thenReturn(Arrays.asList(mockJiraFeature, mockJiraFeature2));

		DataResponse<List<Feature>> result = featureService.getFeatureEpicEstimates(mockComponentId,
				mockJiraFeature.getsTeamID(), Optional.empty(), Optional.of("hours"));
		assertThat(
				"There should only be one result even with multiple same super features over several sub features",
				result.getResult(), hasSize(1));
		assertThat(
				"The total super feature estimate should be the sum total of any similar super features present in the response",
				Integer.valueOf(result.getResult().get(0).getsEstimate()), equalTo(900));
	}

	@Test
	public void testGetCurrentSprintDetail_ValidKanbanTeam_ShowKanban() {
		when(componentRepository.findOne(mockComponentId)).thenReturn(mockComponent);
		when(collectorRepository.findOne(mockItem3.getCollectorId())).thenReturn(mockJiraCollector);
		when(featureRepository.findByUnendingSprintsMinimal((String) notNull()))
				.thenReturn(Arrays.asList(mockJiraFeature3_oldkanban));

		DataResponse<List<Feature>> result = featureService.getCurrentSprintDetail(mockComponentId,
				mockJiraFeature3_oldkanban.getsTeamID(), Optional.of("kanban"));
		assertThat(
				"There should only be one result even with multiple same super features over several sub features",
				result.getResult(), hasSize(1));
		assertThat(
				"The total super feature estimate should be the sum total of any similar super features present in the response",
				result.getResult().get(0).getsSprintName(), equalTo("Sprint " + KANBAN_OLD_SPRINT_ID));
	}
	
	@Test
	public void testGetRelevantStories_Kanban() {
		when(componentRepository.findOne(mockComponentId)).thenReturn(mockComponent);
		when(collectorRepository.findOne(mockItem3.getCollectorId())).thenReturn(mockJiraCollector);
		when(featureRepository.findByUnendingSprints((String) notNull())).thenReturn(Arrays.asList(mockJiraFeature3_oldkanban, mockJiraFeature4_sprintkanban));
		when(featureRepository.findByNullSprints((String) notNull())).thenReturn(Arrays.asList(mockJiraFeature5_nullkanban));
		
		DataResponse<List<Feature>> result = featureService.getRelevantStories(mockComponentId,
				mockJiraFeature3_oldkanban.getsTeamID(), Optional.of("kanban"));
		
		assertEquals(3, result.getResult().size());
	}
	
	@Test
	public void testGetRelevantStories_Scrum() {
		when(componentRepository.findOne(mockComponentId)).thenReturn(mockComponent);
		when(collectorRepository.findOne(mockItem3.getCollectorId())).thenReturn(mockJiraCollector);
		when(featureRepository.findByActiveEndingSprints(Mockito.anyString(), Mockito.anyString())).thenReturn(Arrays.asList(mockJiraFeature, mockJiraFeature2));
		
		DataResponse<List<Feature>> result = featureService.getRelevantStories(mockComponentId,
				mockJiraFeature3_oldkanban.getsTeamID(), Optional.of("scrum"));
		
		assertEquals(2, result.getResult().size());
	}
	
	@Test
	public void testGetAggregatedSprintEstimates_Scrum() {
		when(componentRepository.findOne(mockComponentId)).thenReturn(mockComponent);
		when(collectorRepository.findOne(mockItem3.getCollectorId())).thenReturn(mockJiraCollector);
		when(featureRepository.findByActiveEndingSprintsMinimal(Mockito.anyString(), Mockito.anyString())).thenReturn(Arrays.asList(mockJiraFeature, mockJiraFeature2));
		
		DataResponse<SprintEstimate> result = featureService.getAggregatedSprintEstimates(mockComponentId, mockJiraFeature3_oldkanban.getsTeamID(), Optional.of("scrum"), Optional.of("storypoints"));
		
		assertEquals(0, result.getResult().getOpenEstimate());
		assertEquals(40, result.getResult().getInProgressEstimate());
		assertEquals(50, result.getResult().getCompleteEstimate());
		assertEquals(90, result.getResult().getTotalEstimate());
	}
	
	@Test
	public void testGetAggregatedSprintEstimates_Kanban() {
		when(componentRepository.findOne(mockComponentId)).thenReturn(mockComponent);
		when(collectorRepository.findOne(mockItem3.getCollectorId())).thenReturn(mockJiraCollector);
		when(featureRepository.findByUnendingSprintsMinimal((String) notNull())).thenReturn(Arrays.asList(mockJiraFeature3_oldkanban, mockJiraFeature4_sprintkanban));
		when(featureRepository.findByNullSprintsMinimal((String) notNull())).thenReturn(Arrays.asList(mockJiraFeature5_nullkanban));
		
		DataResponse<SprintEstimate> result = featureService.getAggregatedSprintEstimates(mockComponentId, mockJiraFeature3_oldkanban.getsTeamID(), Optional.of("kanban"), Optional.of("storypoints"));
		
		assertEquals(40, result.getResult().getOpenEstimate());
		assertEquals(50, result.getResult().getInProgressEstimate());
		assertEquals(60, result.getResult().getCompleteEstimate());
		assertEquals(150, result.getResult().getTotalEstimate());
	}
	
	private Feature createFeature(ObjectId collectorId, String sId, String sStatus, String sState, Integer sEstimate, String changeDate, boolean isDeleted, 
			String teamId, String sTeamChangeDate,
			String sSprintID, String sSprintChangeDate, String sSprintBeginDate, String sSprintEndDate,
			String sEpicID, String sEpicChangeDate, String sEpicBeginDate, String sEpicEndDate,
			String sProjectID, String sProjectChangeDate, String sProjectBeginDate, String sProjectEndDate) {
		Feature rt = new Feature();
		rt.setCollectorId(jiraCollectorId);
		
		rt.setsId(sId);
		rt.setsNumber("MOCK-" + sId);
		rt.setsName("Name " + sId);
		rt.setsStatus(sStatus);
		rt.setsState(sState);
		rt.setsEstimate(Integer.toString(sEstimate));
		if (sEstimate != null) {
			rt.setsEstimateTime(sEstimate.intValue() * 60 * 10);
		}
		
		rt.setChangeDate(changeDate);
		rt.setIsDeleted(isDeleted? "True" : "False");
		
		rt.setsOwnersID(OWNER_IDS);
		rt.setsOwnersIsDeleted(OWNER_BOOLS);
		rt.setsOwnersChangeDate(OWNER_DATES);
		rt.setsOwnersState(OWNER_STATES);
		rt.setsOwnersUsername(OWNER_NAMES);
		rt.setsOwnersFullName(OWNER_NAMES);
		rt.setsOwnersShortName(OWNER_NAMES);
		
		// scope owner
		rt.setsTeamIsDeleted("False");
		rt.setsTeamAssetState("Active");
		rt.setsTeamChangeDate(sTeamChangeDate);
		rt.setsTeamName("Team " + teamId);
		rt.setsTeamID(teamId);
		
		// sprint
		rt.setsSprintIsDeleted("False");
		rt.setsSprintChangeDate(sSprintChangeDate);
		rt.setsSprintAssetState("Active");
		rt.setsSprintEndDate(sSprintEndDate);
		rt.setsSprintBeginDate(sSprintBeginDate);
		rt.setsSprintName("Sprint " + sSprintID);
		rt.setsSprintID(sSprintID);
		
		// epic
		rt.setsEpicIsDeleted("False");
		rt.setsEpicChangeDate(sEpicChangeDate);
		rt.setsEpicAssetState("Active");
		rt.setsEpicType("A epic type");
		rt.setsEpicEndDate(sEpicEndDate);
		rt.setsEpicBeginDate(sEpicBeginDate);
		rt.setsEpicName("Epic " + sEpicID);
		rt.setsEpicNumber("EPIC-" + sEpicID);
		rt.setsEpicID(sEpicID);
		
		// scope data
		rt.setsProjectPath("A path");
		rt.setsProjectIsDeleted("False");
		rt.setsProjectState("Active");
		rt.setsProjectChangeDate(sProjectChangeDate);
		rt.setsProjectEndDate(sProjectEndDate);
		rt.setsProjectBeginDate(sProjectBeginDate);
		rt.setsProjectName("Project " + sProjectID);
		rt.setsProjectID(sProjectID);
		
		return rt;
	}
}
