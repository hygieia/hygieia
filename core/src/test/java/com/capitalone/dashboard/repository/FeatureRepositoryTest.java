package com.capitalone.dashboard.repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.capitalone.dashboard.config.MongoConfig;
import com.capitalone.dashboard.model.Feature;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = { MongoConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class FeatureRepositoryTest {
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

	@ClassRule
	public static final EmbeddedMongoDBRule RULE = new EmbeddedMongoDBRule();

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
		mockV1Feature.setSEpicAssetState("Active");
		mockV1Feature.setSEpicBeginDate(generalUseDate);
		mockV1Feature.setSEpicChangeDate(generalUseDate);
		mockV1Feature.setSEpicEndDate(generalUseDate);
		mockV1Feature.setSEpicID("E-12345");
		mockV1Feature.setSEpicIsDeleted("False");
		mockV1Feature.setSEpicName("Test Epic 1");
		mockV1Feature.setSEpicNumber("12938715");
		mockV1Feature.setSEpicType("Portfolio Feature");
		mockV1Feature.setSEstimate("5");
		mockV1Feature.setSId("B-12345");
		mockV1Feature.setSName("Test Story 1");
		mockV1Feature.setSNumber("12345416");
		mockV1Feature.setSOwnersChangeDate(sOwnerDates);
		mockV1Feature.setSOwnersFullName(sOwnerNames);
		mockV1Feature.setSOwnersID(sOwnerIds);
		mockV1Feature.setSOwnersIsDeleted(sOwnerBools);
		mockV1Feature.setSOwnersShortName(sOwnerNames);
		mockV1Feature.setSOwnersState(sOwnerStates);
		mockV1Feature.setSOwnersUsername(sOwnerNames);
		mockV1Feature.setSProjectBeginDate(generalUseDate);
		mockV1Feature.setSProjectChangeDate(generalUseDate);
		mockV1Feature.setSProjectEndDate(generalUseDate);
		mockV1Feature.setSProjectID("Scope:231870");
		mockV1Feature.setSProjectIsDeleted("False");
		mockV1Feature.setSProjectName("Test Scope 1");
		mockV1Feature.setSProjectPath("Top -> Middle -> Bottome -> "
				+ mockV1Feature.getSProjectName());
		mockV1Feature.setSProjectState("Active");
		mockV1Feature.setSSprintAssetState("Inactive");
		mockV1Feature.setSSprintBeginDate(generalUseDate);
		mockV1Feature.setSSprintChangeDate(generalUseDate);
		mockV1Feature.setSSprintEndDate(maxDateWinner);
		mockV1Feature.setSSprintID("Timebox:12781205");
		mockV1Feature.setSSprintIsDeleted("False");
		mockV1Feature.setSSprintName("Test Sprint 1");
		mockV1Feature.setSState("Inactive");
		mockV1Feature.setSStatus("Accepted");
		mockV1Feature.setSTeamAssetState("Active");
		mockV1Feature.setSTeamChangeDate(generalUseDate);
		mockV1Feature.setSTeamID("Team:124127");
		mockV1Feature.setSTeamIsDeleted("False");
		mockV1Feature.setSTeamName("Protectors of Earth");

		// Jira Mock Feature
		// Mock feature 1
		mockJiraFeature = new Feature();
		mockJiraFeature.setCollectorId(jiraCollectorId);
		mockJiraFeature.setIsDeleted("False");
		mockJiraFeature.setChangeDate(maxDateWinner);
		mockJiraFeature.setSEpicAssetState("Active");
		mockJiraFeature.setSEpicBeginDate("");
		mockJiraFeature.setSEpicChangeDate(maxDateWinner);
		mockJiraFeature.setSEpicEndDate("");
		mockJiraFeature.setSEpicID("32112345");
		mockJiraFeature.setSEpicIsDeleted("");
		mockJiraFeature.setSEpicName("Test Epic 1");
		mockJiraFeature.setSEpicNumber("12938715");
		mockJiraFeature.setSEpicType("");
		mockJiraFeature.setSEstimate("40");
		mockJiraFeature.setSId("0812345");
		mockJiraFeature.setSName("Test Story 2");
		mockJiraFeature.setSNumber("12345416");
		mockJiraFeature.setSOwnersChangeDate(sOwnerDates);
		mockJiraFeature.setSOwnersFullName(sOwnerNames);
		mockJiraFeature.setSOwnersID(sOwnerIds);
		mockJiraFeature.setSOwnersIsDeleted(sOwnerBools);
		mockJiraFeature.setSOwnersShortName(sOwnerNames);
		mockJiraFeature.setSOwnersState(sOwnerStates);
		mockJiraFeature.setSOwnersUsername(sOwnerNames);
		mockJiraFeature.setSProjectBeginDate(maxDateWinner);
		mockJiraFeature.setSProjectChangeDate(maxDateWinner);
		mockJiraFeature.setSProjectEndDate(maxDateWinner);
		mockJiraFeature.setSProjectID("583482");
		mockJiraFeature.setSProjectIsDeleted("False");
		mockJiraFeature.setSProjectName("Saiya-jin Warriors");
		mockJiraFeature.setSProjectPath("");
		mockJiraFeature.setSProjectState("Active");
		mockJiraFeature.setSSprintAssetState("Active");
		mockJiraFeature.setSSprintBeginDate(maxDateLoser);
		mockJiraFeature.setSSprintChangeDate(maxDateWinner);
		mockJiraFeature.setSSprintEndDate(currentSprintEndDate);
		mockJiraFeature.setSSprintID("1232512");
		mockJiraFeature.setSSprintIsDeleted("False");
		mockJiraFeature.setSSprintName("Test Sprint 2");
		mockJiraFeature.setSState("Active");
		mockJiraFeature.setSStatus("In Progress");
		mockJiraFeature.setSTeamAssetState("Active");
		mockJiraFeature.setSTeamChangeDate(maxDateWinner);
		mockJiraFeature.setSTeamID("08374321");
		mockJiraFeature.setSTeamIsDeleted("False");
		mockJiraFeature.setSTeamName("Saiya-jin Warriors");

		// Mock feature 2
		mockJiraFeature2 = new Feature();
		mockJiraFeature2.setCollectorId(jiraCollectorId);
		mockJiraFeature2.setIsDeleted("False");
		mockJiraFeature2.setChangeDate(maxDateLoser);
		mockJiraFeature2.setSEpicAssetState("Active");
		mockJiraFeature2.setSEpicBeginDate("");
		mockJiraFeature2.setSEpicChangeDate(maxDateLoser);
		mockJiraFeature2.setSEpicEndDate("");
		mockJiraFeature2.setSEpicID("32112345");
		mockJiraFeature2.setSEpicIsDeleted("");
		mockJiraFeature2.setSEpicName("Test Epic 1");
		mockJiraFeature2.setSEpicNumber("12938715");
		mockJiraFeature2.setSEpicType("");
		mockJiraFeature2.setSEstimate("40");
		mockJiraFeature2.setSId("0812346");
		mockJiraFeature2.setSName("Test Story 3");
		mockJiraFeature2.setSNumber("12345417");
		mockJiraFeature2.setSOwnersChangeDate(sOwnerDates);
		mockJiraFeature2.setSOwnersFullName(sOwnerNames);
		mockJiraFeature2.setSOwnersID(sOwnerIds);
		mockJiraFeature2.setSOwnersIsDeleted(sOwnerBools);
		mockJiraFeature2.setSOwnersShortName(sOwnerNames);
		mockJiraFeature2.setSOwnersState(sOwnerStates);
		mockJiraFeature2.setSOwnersUsername(sOwnerNames);
		mockJiraFeature2.setSProjectBeginDate(maxDateLoser);
		mockJiraFeature2.setSProjectChangeDate(maxDateLoser);
		mockJiraFeature2.setSProjectEndDate(maxDateLoser);
		mockJiraFeature2.setSProjectID("583483");
		mockJiraFeature2.setSProjectIsDeleted("False");
		mockJiraFeature2.setSProjectName("Not Cell!");
		mockJiraFeature2.setSProjectPath("");
		mockJiraFeature2.setSProjectState("Active");
		mockJiraFeature2.setSSprintAssetState("Active");
		mockJiraFeature2.setSSprintBeginDate(maxDateLoser);
		mockJiraFeature2.setSSprintChangeDate(maxDateWinner);
		mockJiraFeature2.setSSprintEndDate(currentSprintEndDate);
		mockJiraFeature2.setSSprintID("1232512");
		mockJiraFeature2.setSSprintIsDeleted("False");
		mockJiraFeature2.setSSprintName("Test Sprint 3");
		mockJiraFeature2.setSState("Active");
		mockJiraFeature2.setSStatus("In Progress");
		mockJiraFeature2.setSTeamAssetState("Active");
		mockJiraFeature2.setSTeamChangeDate(maxDateLoser);
		mockJiraFeature2.setSTeamID("08374329");
		mockJiraFeature2.setSTeamIsDeleted("False");
		mockJiraFeature2.setSTeamName("Interlopers");

		// Mock feature 3
		mockJiraFeature3 = new Feature();
		mockJiraFeature3.setCollectorId(jiraCollectorId);
		mockJiraFeature3.setIsDeleted("False");
		mockJiraFeature3.setChangeDate(generalUseDate2);
		mockJiraFeature3.setSEpicAssetState("Active");
		mockJiraFeature3.setSEpicBeginDate("");
		mockJiraFeature3.setSEpicChangeDate(maxDateLoser);
		mockJiraFeature3.setSEpicEndDate("");
		mockJiraFeature3.setSEpicID("32112345");
		mockJiraFeature3.setSEpicIsDeleted("");
		mockJiraFeature3.setSEpicName("Test Epic 1");
		mockJiraFeature3.setSEpicNumber("12938715");
		mockJiraFeature3.setSEpicType("");
		mockJiraFeature3.setSEstimate("80");
		mockJiraFeature3.setSId("0812342");
		mockJiraFeature3.setSName("Test Story 4");
		mockJiraFeature3.setSNumber("12345412");
		mockJiraFeature3.setSOwnersChangeDate(sOwnerDates);
		mockJiraFeature3.setSOwnersFullName(sOwnerNames);
		mockJiraFeature3.setSOwnersID(sOwnerIds);
		mockJiraFeature3.setSOwnersIsDeleted(sOwnerBools);
		mockJiraFeature3.setSOwnersShortName(sOwnerNames);
		mockJiraFeature3.setSOwnersState(sOwnerStates);
		mockJiraFeature3.setSOwnersUsername(sOwnerNames);
		mockJiraFeature3.setSProjectBeginDate(maxDateLoser);
		mockJiraFeature3.setSProjectChangeDate(maxDateLoser);
		mockJiraFeature3.setSProjectEndDate(maxDateLoser);
		mockJiraFeature3.setSProjectID("583483");
		mockJiraFeature3.setSProjectIsDeleted("False");
		mockJiraFeature3.setSProjectName("Not Cell!");
		mockJiraFeature3.setSProjectPath("");
		mockJiraFeature3.setSProjectState("Active");
		mockJiraFeature3.setSSprintAssetState("Active");
		mockJiraFeature3.setSSprintBeginDate(maxDateLoser);
		mockJiraFeature3.setSSprintChangeDate(maxDateWinner);
		mockJiraFeature3.setSSprintEndDate(currentSprintEndDate);
		mockJiraFeature3.setSSprintID("1232512");
		mockJiraFeature3.setSSprintIsDeleted("False");
		mockJiraFeature3.setSSprintName("Test Sprint 3");
		mockJiraFeature3.setSState("Active");
		mockJiraFeature3.setSStatus("In Progress");
		mockJiraFeature3.setSTeamAssetState("Active");
		mockJiraFeature3.setSTeamChangeDate(maxDateLoser);
		mockJiraFeature3.setSTeamID("08374329");
		mockJiraFeature3.setSTeamIsDeleted("False");
		mockJiraFeature3.setSTeamName("Interlopers");

		// Mock feature 4
		mockJiraFeature4 = new Feature();
		mockJiraFeature4.setCollectorId(jiraCollectorId);
		mockJiraFeature4.setIsDeleted("False");
		mockJiraFeature4.setChangeDate(generalUseDate3);
		mockJiraFeature4.setSEpicAssetState("Active");
		mockJiraFeature4.setSEpicBeginDate("");
		mockJiraFeature4.setSEpicChangeDate(maxDateLoser);
		mockJiraFeature4.setSEpicEndDate("");
		mockJiraFeature4.setSEpicID("32112345");
		mockJiraFeature4.setSEpicIsDeleted("");
		mockJiraFeature4.setSEpicName("Test Epic 1");
		mockJiraFeature4.setSEpicNumber("12938715");
		mockJiraFeature4.setSEpicType("");
		mockJiraFeature4.setSEstimate("45");
		mockJiraFeature4.setSId("0812344");
		mockJiraFeature4.setSName("Test Story 4");
		mockJiraFeature4.setSNumber("12345414");
		mockJiraFeature4.setSOwnersChangeDate(sOwnerDates);
		mockJiraFeature4.setSOwnersFullName(sOwnerNames);
		mockJiraFeature4.setSOwnersID(sOwnerIds);
		mockJiraFeature4.setSOwnersIsDeleted(sOwnerBools);
		mockJiraFeature4.setSOwnersShortName(sOwnerNames);
		mockJiraFeature4.setSOwnersState(sOwnerStates);
		mockJiraFeature4.setSOwnersUsername(sOwnerNames);
		mockJiraFeature4.setSProjectBeginDate(maxDateLoser);
		mockJiraFeature4.setSProjectChangeDate(maxDateLoser);
		mockJiraFeature4.setSProjectEndDate(maxDateLoser);
		mockJiraFeature4.setSProjectID("583483");
		mockJiraFeature4.setSProjectIsDeleted("False");
		mockJiraFeature4.setSProjectName("Not Cell!");
		mockJiraFeature4.setSProjectPath("");
		mockJiraFeature4.setSProjectState("Active");
		mockJiraFeature4.setSSprintAssetState("Active");
		mockJiraFeature4.setSSprintBeginDate(maxDateLoser);
		mockJiraFeature4.setSSprintChangeDate(maxDateWinner);
		mockJiraFeature4.setSSprintEndDate(currentSprintEndDate);
		mockJiraFeature4.setSSprintID("1232512");
		mockJiraFeature4.setSSprintIsDeleted("False");
		mockJiraFeature4.setSSprintName("Test Sprint 3");
		mockJiraFeature4.setSState("Active");
		mockJiraFeature4.setSStatus("In Progress");
		mockJiraFeature4.setSTeamAssetState("Active");
		mockJiraFeature4.setSTeamChangeDate(maxDateLoser);
		mockJiraFeature4.setSTeamID("08374329");
		mockJiraFeature4.setSTeamIsDeleted("False");
		mockJiraFeature4.setSTeamName("Interlopers");
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
				featureRepo.getFeatureIdById(testStoryId).get(0).getSId().toString());
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

		assertEquals(
				"Expected top ordered sprint story ID did not match actual top ordered sprint story ID",
				testStoryId, featureRepo.queryByOrderBySStatusDesc(testTeamID, maxDateWinner)
						.get(0).getSId().toString());
	}

	@Test
	public void testGetCurrentSprintDetail_HappyPath() {
		featureRepo.save(mockV1Feature);
		featureRepo.save(mockJiraFeature);
		featureRepo.save(mockJiraFeature2);
		String testTeamId = "08374321";
		String testSprintName = "Test Sprint 2";
		assertEquals("Expected current sprint detail did not match actual current sprint detail",
				testSprintName, featureRepo.getCurrentSprintDetail(testTeamId, maxDateWinner)
						.get(0).getSSprintName());
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
				featureRepo.getInProgressFeaturesEstimatesByTeamId(mockJiraFeature3.getSTeamID(),
						currentSprintEndDate).size());
	}
}