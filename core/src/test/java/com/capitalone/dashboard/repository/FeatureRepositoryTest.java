package com.capitalone.dashboard.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.capitalone.dashboard.model.Feature;

public class FeatureRepositoryTest extends FongoBaseRepositoryTest {
	private static Feature mockV1Feature;
	private static Feature mockJiraFeature;
	private static Feature mockJiraFeature2;
	private static Feature mockJiraFeature3;
	private static Feature mockJiraFeature4;
	private static final String generalUseDate = "2015-11-01T00:00:00Z";
	private static final String generalUseDate2 = "2015-12-01T00:00:00Z";
	private static final String generalUseDate3 = "2015-12-15T00:00:00Z";
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static Calendar cal = Calendar.getInstance();
	private static final String maxDateWinner = df.format(new Date());
	private static String maxDateLoser = new String();
	private static String currentSprintEndDate = new String();
	private static final ObjectId jiraCollectorId = new ObjectId();
//	private static final ObjectId v1CollectorId = new ObjectId();

	@Autowired
	private FeatureRepository featureRepo;

	@Before
	public void setUp() {
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
		mockV1Feature.setCollectorId(jiraCollectorId);
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
		mockJiraFeature2.setCollectorId(jiraCollectorId);
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
		mockJiraFeature2.setsTeamChangeDate(maxDateLoser);
		mockJiraFeature2.setsTeamID("08374329");
		mockJiraFeature2.setsTeamIsDeleted("False");
		mockJiraFeature2.setsTeamName("Interlopers");

		// Mock feature 3
		mockJiraFeature3 = new Feature();
		mockJiraFeature3.setCollectorId(jiraCollectorId);
		mockJiraFeature3.setIsDeleted("False");
		mockJiraFeature3.setChangeDate(generalUseDate2);
		mockJiraFeature3.setsEpicAssetState("Active");
		mockJiraFeature3.setsEpicBeginDate("");
		mockJiraFeature3.setsEpicChangeDate(maxDateLoser);
		mockJiraFeature3.setsEpicEndDate("");
		mockJiraFeature3.setsEpicID("32112345");
		mockJiraFeature3.setsEpicIsDeleted("");
		mockJiraFeature3.setsEpicName("Test Epic 1");
		mockJiraFeature3.setsEpicNumber("12938715");
		mockJiraFeature3.setsEpicType("");
		mockJiraFeature3.setsEstimate("80");
		mockJiraFeature3.setsId("0812342");
		mockJiraFeature3.setsName("Test Story 4");
		mockJiraFeature3.setsNumber("12345412");
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
		mockJiraFeature3.setsSprintBeginDate(maxDateLoser);
		mockJiraFeature3.setsSprintChangeDate(maxDateWinner);
		mockJiraFeature3.setsSprintEndDate(currentSprintEndDate);
		mockJiraFeature3.setsSprintID("1232512");
		mockJiraFeature3.setsSprintIsDeleted("False");
		mockJiraFeature3.setsSprintName("Test Sprint 3");
		mockJiraFeature3.setsState("Active");
		mockJiraFeature3.setsStatus("In Progress");
		mockJiraFeature3.setsTeamAssetState("Active");
		mockJiraFeature3.setsTeamChangeDate(maxDateLoser);
		mockJiraFeature3.setsTeamID("08374329");
		mockJiraFeature3.setsTeamIsDeleted("False");
		mockJiraFeature3.setsTeamName("Interlopers");

		// Mock feature 4
		mockJiraFeature4 = new Feature();
		mockJiraFeature4.setCollectorId(jiraCollectorId);
		mockJiraFeature4.setIsDeleted("False");
		mockJiraFeature4.setChangeDate(generalUseDate3);
		mockJiraFeature4.setsEpicAssetState("Active");
		mockJiraFeature4.setsEpicBeginDate("");
		mockJiraFeature4.setsEpicChangeDate(maxDateLoser);
		mockJiraFeature4.setsEpicEndDate("");
		mockJiraFeature4.setsEpicID("32112345");
		mockJiraFeature4.setsEpicIsDeleted("");
		mockJiraFeature4.setsEpicName("Test Epic 1");
		mockJiraFeature4.setsEpicNumber("12938715");
		mockJiraFeature4.setsEpicType("");
		mockJiraFeature4.setsEstimate("45");
		mockJiraFeature4.setsId("0812344");
		mockJiraFeature4.setsName("Test Story 4");
		mockJiraFeature4.setsNumber("12345414");
		mockJiraFeature4.setsOwnersChangeDate(sOwnerDates);
		mockJiraFeature4.setsOwnersFullName(sOwnerNames);
		mockJiraFeature4.setsOwnersID(sOwnerIds);
		mockJiraFeature4.setsOwnersIsDeleted(sOwnerBools);
		mockJiraFeature4.setsOwnersShortName(sOwnerNames);
		mockJiraFeature4.setsOwnersState(sOwnerStates);
		mockJiraFeature4.setsOwnersUsername(sOwnerNames);
		mockJiraFeature4.setsProjectBeginDate(maxDateLoser);
		mockJiraFeature4.setsProjectChangeDate(maxDateLoser);
		mockJiraFeature4.setsProjectEndDate(maxDateLoser);
		mockJiraFeature4.setsProjectID("583483");
		mockJiraFeature4.setsProjectIsDeleted("False");
		mockJiraFeature4.setsProjectName("Not Cell!");
		mockJiraFeature4.setsProjectPath("");
		mockJiraFeature4.setsProjectState("Active");
		mockJiraFeature4.setsSprintAssetState("Active");
		mockJiraFeature4.setsSprintBeginDate(maxDateLoser);
		mockJiraFeature4.setsSprintChangeDate(maxDateWinner);
		mockJiraFeature4.setsSprintEndDate(currentSprintEndDate);
		mockJiraFeature4.setsSprintID("1232512");
		mockJiraFeature4.setsSprintIsDeleted("False");
		mockJiraFeature4.setsSprintName("Test Sprint 3");
		mockJiraFeature4.setsState("Active");
		mockJiraFeature4.setsStatus("In Progress");
		mockJiraFeature4.setsTeamAssetState("Active");
		mockJiraFeature4.setsTeamChangeDate(maxDateLoser);
		mockJiraFeature4.setsTeamID("08374329");
		mockJiraFeature4.setsTeamIsDeleted("False");
		mockJiraFeature4.setsTeamName("Interlopers");
	}

	@After
	public void tearDown() {
		mockV1Feature = null;
		mockJiraFeature = null;
		mockJiraFeature2 = null;
		mockJiraFeature3 = null;
		mockJiraFeature4 = null;
		featureRepo.deleteAll();
	}

	@Test
	public void validateConnectivity_HappyPath() {
		featureRepo.save(mockV1Feature);
		featureRepo.save(mockJiraFeature);
		featureRepo.save(mockJiraFeature2);

		assertTrue(
				"Happy-path MongoDB connectivity validation for the FeatureRepository has failed",
				featureRepo.findAll().iterator().hasNext());
	}

	@Test
	public void testGetFeatureIdById_HappyPath() {
		featureRepo.save(mockV1Feature);
		featureRepo.save(mockJiraFeature);
		featureRepo.save(mockJiraFeature2);
		String testStoryId = "0812345";

		assertEquals("Expected feature ID did not match actual feature ID", testStoryId,
				featureRepo.getFeatureIdById(testStoryId).get(0).getsId().toString());
	}

	@Test
	public void testFindTopByOrderByChangeDateDesc_HappyPath() {
		featureRepo.save(mockV1Feature);
		featureRepo.save(mockJiraFeature);
		featureRepo.save(mockJiraFeature2);
		featureRepo.save(mockJiraFeature3);
		featureRepo.save(mockJiraFeature4);

		assertEquals(
				"Expected feature max change date did not match actual feature max change date",
				maxDateWinner,
				featureRepo
						.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
								jiraCollectorId, maxDateLoser).get(0).getChangeDate().toString());
	}

	@Test
	public void testFindTopByOrderByChangeDateDesc_BVA() {
		featureRepo.save(mockV1Feature);
		featureRepo.save(mockJiraFeature);
		featureRepo.save(mockJiraFeature2);
		featureRepo.save(mockJiraFeature3);
		featureRepo.save(mockJiraFeature4);
		// Setting slight differences in testable values for last change date
		int lastDigit = Integer.parseInt(maxDateWinner.substring(maxDateWinner.length() - 3,
				maxDateWinner.length() - 1));
		int biggerThanDigit = lastDigit + 1;
		int smallerThanDigit = lastDigit - 1;
		String biggerThanDigitConv;
		String smallerThanDigitConv;
		if (biggerThanDigit < 10) {
			biggerThanDigitConv = "0" + Integer.toString(biggerThanDigit);
		} else {
			biggerThanDigitConv = Integer.toString(biggerThanDigit);
		}
		if (smallerThanDigit < 10) {
			smallerThanDigitConv = "0" + Integer.toString(smallerThanDigit);
		} else {
			smallerThanDigitConv = Integer.toString(smallerThanDigit);
		}
		String biggerThanWinner = maxDateWinner.substring(0, maxDateWinner.length() - 3)
				+ biggerThanDigitConv + "Z";
		String smallerThanWinner = maxDateWinner.substring(0, maxDateWinner.length() - 3)
				+ smallerThanDigitConv + "Z";

		assertEquals(
				"Actual size should result in a size of 0",
				0,
				featureRepo.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
						jiraCollectorId, maxDateWinner).size());
		assertEquals(
				"Actual size should result in a size of 0",
				0,
				featureRepo.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
						jiraCollectorId, biggerThanWinner).size());
		assertEquals(
				"Actual size should result in a size of 1",
				1,
				featureRepo.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
						jiraCollectorId, smallerThanWinner).size());
	}

	@Test
	public void testFindTopByOrderByChangeDateDesc_RealisticDeltaStartDate() {
		featureRepo.save(mockV1Feature);
		featureRepo.save(mockJiraFeature);
		featureRepo.save(mockJiraFeature2);
		featureRepo.save(mockJiraFeature3);
		featureRepo.save(mockJiraFeature4);
		
		assertTrue(
				"Actual size should result in a size of 1",
				featureRepo.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
						jiraCollectorId, "2015-10-01T00:00:00Z").size() == 1);
		assertTrue(
				"Actual size should result in a size of 0",
				featureRepo.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
						jiraCollectorId, maxDateWinner).size() == 0);
		assertTrue(
				"Expected response of the maximum change date did not match the actual match change date",
				featureRepo
						.findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
								jiraCollectorId, maxDateLoser).get(0).getChangeDate().toString()
						.equalsIgnoreCase(maxDateWinner));
	}

	@Test
	public void testGetSprintStoriesByTeamId_HappyPath() {
		featureRepo.save(mockV1Feature);
		featureRepo.save(mockJiraFeature);
		featureRepo.save(mockJiraFeature2);
		String testTeamID = "08374321";
		String testStoryId = "0812345";
		String testProjectID = "583482";

		assertEquals(
				"Expected top ordered sprint story ID did not match actual top ordered sprint story ID",
				testStoryId, featureRepo.findByActiveEndingSprints(testTeamID, testProjectID, jiraCollectorId, maxDateWinner, false)
						.get(0).getsId().toString());
	}

	@Test
	public void testGetCurrentSprintDetail_HappyPath() {
		featureRepo.save(mockV1Feature);
		featureRepo.save(mockJiraFeature);
		featureRepo.save(mockJiraFeature2);
		String testTeamId = "08374321";
		String testProjectID = "583482";
		String testSprintName = "Test Sprint 2";
		assertEquals("Expected current sprint detail did not match actual current sprint detail",
				testSprintName, featureRepo.findByActiveEndingSprints(testTeamId, testProjectID, jiraCollectorId, maxDateWinner, true)
						.get(0).getsSprintName());
	}

	@Test
	public void testGetInProgressFeaturesEstimatesByTeamId_MultipleValidStories() {
		featureRepo.save(mockJiraFeature);
		featureRepo.save(mockJiraFeature2);
		featureRepo.save(mockJiraFeature3);
		featureRepo.save(mockJiraFeature4);

		assertEquals(
				"The size of the actual response was not expected",
				3,
				featureRepo.findByActiveEndingSprints(mockJiraFeature3.getsTeamID(), mockJiraFeature3.getsProjectID(), jiraCollectorId,
						currentSprintEndDate, true).size());
	}
}