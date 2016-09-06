package com.capitalone.dashboard.client.story;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.codehaus.jettison.json.JSONArray;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.BasicWatchers;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.api.domain.User;
import com.capitalone.dashboard.client.JiraClient;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.FeatureStatus;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.CoreFeatureSettings;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.FeatureSettings;

@RunWith(MockitoJUnitRunner.class)
public class StoryDataClientImplTests {
	private static final ObjectId JIRA_COLLECTORID = new ObjectId("ABCDEF0123456789ABCDEF01");
	
	private static final BasicProject PROJECT1 = new BasicProject(URI.create("http://my.jira.com/rest/api/2/project/100"), "project1", Long.valueOf(100L), "projectname1");
	
	
	private static final User USER1 = new User(URI.create("http://my.jira.com/rest/api/2/user?username=billy"), "Billy", "Billy Bob", "Billy@foo.com", null, getAvatarUris(), null);
	
	private static final IssueType ISSUETYPE1 = new IssueType(URI.create("http://my.jira.com/rest/api/2/issuetype/10"), Long.valueOf(10), "Story", false, "issuetype10", null);
	
	private static final Status STATUS_TODO = new Status(URI.create("http://my.jira.com/rest/api/2/status/21"), Long.valueOf(21), "OPEN", "OPEN", null);
	private static final Status STATUS_IN_PROGRESS = new Status(URI.create("http://my.jira.com/rest/api/2/status/22"), Long.valueOf(22), "IN PROGRESS", "IN PROGRESS", null);
	private static final Status STATUS_DONE = new Status(URI.create("http://my.jira.com/rest/api/2/status/23"), Long.valueOf(23), "CLOSED", "CLOSED", null);
	
	CoreFeatureSettings coreFeatureSettings;
	FeatureSettings featureSettings;
	@Mock FeatureRepository featureRepo;
	@Mock FeatureCollectorRepository featureCollectorRepository;
	@Mock JiraClient jiraClient;
	@Captor ArgumentCaptor<List<Feature>> captor;
	
	StoryDataClientImpl storyDataClient;
	
	@Before
	public final void init() {
		coreFeatureSettings = new CoreFeatureSettings();
		featureSettings = new FeatureSettings();
		
		coreFeatureSettings.setTodoStatuses(Arrays.asList("OPEN"));
		coreFeatureSettings.setDoingStatuses(Arrays.asList("IN PROGRESS"));
		coreFeatureSettings.setDoneStatuses(Arrays.asList("CLOSED"));
		
		featureSettings.setJiraIssueTypeId("Story");
		featureSettings.setJiraSprintDataFieldName("custom_sprint");
		featureSettings.setJiraEpicIdFieldName("custom_epic");
		featureSettings.setJiraStoryPointsFieldName("custom_storypoints");
		featureSettings.setDeltaStartDate("2016-03-01T00:00:00.000000");
		featureSettings.setPageSize(25);
		featureSettings.setJiraBaseUrl("https://jira.atlassian.com/");
		featureSettings.setJiraQueryEndpoint("rest/api/latest/");
		
		storyDataClient = new StoryDataClientImpl(coreFeatureSettings, featureSettings, featureRepo, featureCollectorRepository, jiraClient);
		
		FeatureCollector jira = new FeatureCollector();
		jira.setId(JIRA_COLLECTORID);
		
		Mockito.when(featureCollectorRepository.findByName(Mockito.eq(FeatureCollectorConstants.JIRA))).thenReturn(jira);
		Mockito.when(jiraClient.getPageSize()).thenReturn(25);
		

	}
	
	@Test
	public void testUpdateStoryInformation_NoPage() {
		
		// This is actually how the data comes back from jira
		String sprintRaw = "com.atlassian.greenhopper.service.sprint.Sprint@2189d27[id=2144,rapidViewId=1645,state=OPEN,name=Sprint 18,startDate=2016-05-31T14:06:46.350-04:00,endDate=2016-06-16T17:06:00.000-04:00,completeDate=2016-06-20T14:21:57.131-04:00,sequence=2144]";
		String sprintRaw2 = "com.atlassian.greenhopper.service.sprint.Sprint@2189d27[id=2144,rapidViewId=1645,state=OPEN,name=Sprint 17,startDate=2016-04-31T14:06:46.350-04:00,endDate=2016-05-31T17:06:00.000-04:00,completeDate=2016-05-31T14:21:57.131-04:00,sequence=2144]";

		JSONArray jsonA = new JSONArray();
		jsonA.put(sprintRaw);
		jsonA.put(sprintRaw2);
		
		List<Issue> jiraClientResponse = Arrays.asList(
				createIssue(1001, 10000000, STATUS_TODO, createTimeTracking(5 * 60, 4 * 60, 1 * 60), 
						Arrays.asList(createField("custom_sprint", "List", jsonA), createField("custom_storypoints", "Integer", 3)))
				);
		
		Mockito.when(jiraClient.getIssues(Mockito.anyLong(), Mockito.eq(0))).thenReturn(jiraClientResponse);
		
		int cnt = storyDataClient.updateStoryInformation();
		Mockito.verify(featureRepo).save(captor.capture());
		
		assertEquals(1, cnt);
		Feature feature1 = captor.getAllValues().get(0).get(0);
		assertEquals(JIRA_COLLECTORID, feature1.getCollectorId());
		assertEquals("1001", feature1.getsId());
		
		// processFeatureData
		assertEquals("key1001", feature1.getsNumber());
		assertEquals("summary1001", feature1.getsName());
		assertEquals(FeatureStatus.BACKLOG.getStatus(), feature1.getsStatus());
		assertEquals(FeatureStatus.BACKLOG.getStatus(), feature1.getsState());
		assertEquals("3", feature1.getsEstimate());
		assertEquals(Integer.valueOf(5 * 60), feature1.getsEstimateTime());
		assertEquals("False", feature1.getIsDeleted());
		assertEquals("project1", feature1.getsProjectID());
		assertEquals("projectname1", feature1.getsProjectName());
		assertNotNull(feature1.getsProjectBeginDate());
		assertNotNull(feature1.getsProjectEndDate());
		assertNotNull(feature1.getsProjectChangeDate());
		assertNotNull(feature1.getsProjectState());
		assertEquals("False", feature1.getsProjectIsDeleted());
		assertNotNull(feature1.getsProjectPath());
		assertEquals("100", feature1.getsTeamID());
		assertEquals("projectname1", feature1.getsTeamName());
		assertNotNull(feature1.getsTeamChangeDate());
		assertNotNull(feature1.getsTeamAssetState());
		assertEquals("False", feature1.getsTeamIsDeleted());
		assertEquals("Active", feature1.getsOwnersState().iterator().next());
		assertEquals(Collections.<String>emptyList(), feature1.getsOwnersChangeDate());
		assertEquals(Collections.<String>emptyList(), feature1.getsOwnersChangeDate());
		
		// processSprintData
		assertEquals("2144", feature1.getsSprintID());
		assertEquals("Sprint 18", feature1.getsSprintName());
		assertEquals(dateLocal("2016-05-31T14:06:46.350-04:00") + "0000", feature1.getsSprintBeginDate());
		assertEquals(dateLocal("2016-06-16T17:06:00.000-04:00") + "0000", feature1.getsSprintEndDate());
		assertEquals("OPEN", feature1.getsSprintAssetState());
		assertNotNull(feature1.getsSprintChangeDate());
		assertEquals("False", feature1.getsSprintIsDeleted());
		
		// processAssigneeData
		assertEquals(Arrays.asList("Billy"), feature1.getsOwnersShortName());
		assertEquals(Arrays.asList("Billy"), feature1.getsOwnersUsername());
		assertEquals(Arrays.asList("Billy"), feature1.getsOwnersID());
		assertEquals(Arrays.asList("Billy Bob"), feature1.getsOwnersFullName());
		
		// epic data test elsewhere
	}
	
	@Test
	public void testUpdateStoryInformation_WithPage() {
		featureSettings.setPageSize(2);
		Mockito.when(jiraClient.getPageSize()).thenReturn(2);
		
		// This is actually how the data comes back from jira
		String sprintRaw = "com.atlassian.greenhopper.service.sprint.Sprint@2189d27[id=2144,rapidViewId=1645,state=OPEN,name=Sprint 18,startDate=2016-05-31T14:06:46.350-04:00,endDate=2016-06-16T17:06:00.000-04:00,completeDate=2016-06-20T14:21:57.131-04:00,sequence=2144]";
		JSONArray jsonA = new JSONArray();
		jsonA.put(sprintRaw);
		
		List<Issue> jiraClientResponse = Arrays.asList(
				createIssue(1001, 10000000, STATUS_TODO, createTimeTracking(5 * 60, 4 * 60, 1 * 60), Arrays.asList(createField("custom_sprint", "List", jsonA))),
				createIssue(1002, 10000000, STATUS_TODO, createTimeTracking(5 * 60, 4 * 60, 1 * 60), Arrays.asList(createField("custom_sprint", "List", jsonA))),
				createIssue(1003, 10000000, STATUS_DONE, createTimeTracking(5 * 60, 4 * 60, 1 * 60), Arrays.asList(createField("custom_sprint", "List", jsonA)))
				);
		
		Mockito.when(jiraClient.getIssues(Mockito.anyLong(), Mockito.eq(0))).thenReturn(jiraClientResponse.subList(0, 2));
		Mockito.when(jiraClient.getIssues(Mockito.anyLong(), Mockito.eq(2))).thenReturn(jiraClientResponse.subList(2, 3));
		
		int cnt = storyDataClient.updateStoryInformation();
		Mockito.verify(featureRepo, Mockito.times(2)).save(captor.capture());
		
		assertEquals(3, cnt);
		Feature feature1 = captor.getAllValues().get(0).get(0);
		assertEquals(JIRA_COLLECTORID, feature1.getCollectorId());
		assertEquals("1001", feature1.getsId());
		
		Feature feature2 = captor.getAllValues().get(0).get(1);
		assertEquals(JIRA_COLLECTORID, feature2.getCollectorId());
		assertEquals("1002", feature2.getsId());
		
		Feature feature3 = captor.getAllValues().get(1).get(0);
		assertEquals(JIRA_COLLECTORID, feature3.getCollectorId());
		assertEquals("1003", feature3.getsId());
	}
	
	@Test
	public void testUpdateStoryInformation_WithEpic() {
		
		// This is actually how the data comes back from jira
		List<Issue> jiraClientResponse = Arrays.asList(
				createIssue(1001, 10000000, STATUS_DONE, createTimeTracking(5 * 60, 4 * 60, 1 * 60), 
						Arrays.asList(createField("custom_epic", "String", "1002")))
				);
		
		Issue jiraClientEpicResponse = createIssue(1002, 1467739128322L, STATUS_IN_PROGRESS, null, null);
		
		Mockito.when(jiraClient.getIssues(Mockito.anyLong(), Mockito.eq(0))).thenReturn(jiraClientResponse);
		Mockito.when(jiraClient.getEpic(Mockito.eq("1002"))).thenReturn(jiraClientEpicResponse);
		
		int cnt = storyDataClient.updateStoryInformation();
		Mockito.verify(featureRepo).save(captor.capture());
		
		assertEquals(1, cnt);
		Feature feature1 = captor.getAllValues().get(0).get(0);
		assertEquals(JIRA_COLLECTORID, feature1.getCollectorId());
		assertEquals("1001", feature1.getsId());
		
		assertEquals("1002", feature1.getsEpicID());
		assertEquals("key1002", feature1.getsEpicNumber());
		assertEquals("summary1002", feature1.getsEpicName());
		assertEquals(dateLocal("2016-06-24T03:32:08.322-00:00") + "0000", feature1.getsEpicBeginDate());
		assertEquals(dateLocal("2016-07-17T07:05:28.322-00:00") + "0000", feature1.getsEpicEndDate());
		assertEquals("IN PROGRESS", feature1.getsEpicAssetState());
		
		assertNotNull(feature1.getsEpicType());
		assertNotNull(feature1.getsEpicChangeDate());
		assertEquals("False", feature1.getsEpicIsDeleted());
	}
	
	private Issue createIssue(long id, long updateDate, Status status, TimeTracking timeTracking, Collection<IssueField> issueFields) {
		String idStr = Long.valueOf(id).toString();
		
		Issue rt = new Issue(
				"summary" + idStr, // summary
				URI.create("http://my.jira.com/rest/api/2/issue/" + idStr), // self
				"key" + idStr, // key
				Long.valueOf(id), // id
				PROJECT1, // project
				ISSUETYPE1, //issueType
				status, //status
				"description" + idStr, // description
				null, // priority
				null, // resolution
				Collections.emptyList(), // attachments
				USER1, // reporter
				USER1, // assignee
				new DateTime(updateDate - 1000000000, DateTimeZone.UTC), // creationDate
				new DateTime(updateDate, DateTimeZone.UTC), // updateDate
				new DateTime(updateDate + 1000000000, DateTimeZone.UTC), // dueDate
				Collections.emptyList(), // affectedVersions
				Collections.emptyList(), // fixVersions
				Collections.emptyList(), // components
				timeTracking, // timeTracking
				issueFields, // issueFields,
				Arrays.asList(Comment.valueOf("A comment")), // comments
				null, // transitionUri
				null, // issueLinks
				new BasicVotes(null, 0, false), // votes
				Collections.emptyList(), // worklogs
				new BasicWatchers(null, false, 0), // watchers
				null, // expandos
				null, // subtasks
				null, // changelog
				null, // operations
				new HashSet<>(Arrays.asList("label" + idStr)) // labels		
		);
		
		return rt;
	}
	
	private IssueField createField(String id, String type, Object value) {
		return new IssueField(id, "name" + id, type, value);
	}
	
	private TimeTracking createTimeTracking(Integer originalEstimateMinutes, Integer remainingEstimateMinutes, Integer timeSpentMinutes) {
		return new TimeTracking(originalEstimateMinutes, remainingEstimateMinutes, timeSpentMinutes);
	}
	
	private static Map<String, URI> getAvatarUris() {
		Map<String, URI> rt = new HashMap<>();
		
		rt.put(User.S48_48, URI.create("http://foobar.com"));
		
		return rt;
	}
	
	private String dateLocal(String date) {
		DateTime dt = ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(date);
		return ISODateTimeFormat.dateHourMinuteSecondMillis().print(dt);
	}
}
