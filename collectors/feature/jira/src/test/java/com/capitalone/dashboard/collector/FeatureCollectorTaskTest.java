package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.common.TestUtils;
import com.capitalone.dashboard.config.FongoConfig;
import com.capitalone.dashboard.config.TestConfig;

import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.repository.FeatureBoardRepository;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import com.capitalone.dashboard.testutil.GsonUtil;
import com.capitalone.dashboard.util.Supplier;
import com.github.fakemongo.junit.FongoRule;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, FongoConfig.class})
@DirtiesContext
public class FeatureCollectorTaskTest {

    private String TEAM_TYPE_KANBAN = "kanban";
    private String TEAM_TYPE_SCRUM = "scrum";
    @Mock
    private DefaultJiraClient defaultJiraClient;
    @Autowired
    private FeatureCollectorTask featureCollectorTask;
    @Rule
    public FongoRule fongoRule = new FongoRule();
    @Mock
    private Supplier<RestOperations> restOperationsSupplier = mock(Supplier.class);
    @Mock
    private RestOperations rest = mock(RestOperations.class);
    @Autowired
    private FeatureSettings featureSettings;
    @Autowired
    private FeatureCollectorRepository featureCollectorRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ScopeRepository projectRepository;
    @Autowired
    private FeatureRepository featureRepository;
    @Mock
    private FeatureBoardRepository featureBoardRepository =  mock(FeatureBoardRepository.class);;

    private FeatureCollector featureCollector;

    @Before
    public void loadStuff() throws IOException {
        TestUtils.loadCollectorFeature(featureCollectorRepository);
        TestUtils.loadTeams(teamRepository);
        TestUtils.loadScope(projectRepository);
        TestUtils.loadFeature(featureRepository);
        when(restOperationsSupplier.get()).thenReturn(rest);
        defaultJiraClient = new DefaultJiraClient(featureSettings,restOperationsSupplier);
        featureSettings.setJiraBoardAsTeam(true);
        featureSettings.setCollectorItemOnlyUpdate(false);
        featureCollectorTask = new FeatureCollectorTask(null,featureRepository,teamRepository,projectRepository,featureCollectorRepository,featureSettings,defaultJiraClient, featureBoardRepository);

        featureCollector = featureCollectorTask.getCollector();
        featureCollector.setId(new ObjectId("5c38f2f087cd1f53ca81bd3d"));
    }
    @Test
    public void shouldCollect() throws IOException {
        doReturn(new ResponseEntity<>(getExpectedJSON("response/projectresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("api/2/project"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/boardsresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("/rest/agile/1.0/board"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));

        featureCollectorTask.collect(featureCollector);
        assertNotNull(teamRepository.findByTeamId("8"));
        assertNotNull(teamRepository.findByTeamId("16"));
        assertNotNull(teamRepository.findByTeamId("17"));
        assertNotNull(teamRepository.findByTeamId("125"));

    }
    @Test
    public void validateTeamCleanUp() throws IOException {
        doReturn(new ResponseEntity<>(getExpectedJSON("response/projectresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("api/2/project"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        String issueResponseQuery = "&fields=id,key,issuetype,status,summary,created,updated,project,issuelinks,assignee,sprint,epic,aggregatetimeoriginalestimate,timeoriginalestimate,customfield_11248,customfield_10007,customfield_10003,customfield_10004";
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-combo.json"), HttpStatus.OK)).when(rest).exchange(contains(issueResponseQuery), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-empty.json"), HttpStatus.OK)).when(rest).exchange(contains("/issue?jql=issueType"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/boardsresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("/rest/agile/1.0/board?"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));

        featureCollector.setLastExecuted(System.currentTimeMillis());
        featureCollectorTask.collect(featureCollector);
        assertNull(teamRepository.findByTeamId("999"));
        assertNull(teamRepository.findByTeamId("998"));

    }
   @Test
    public void addBoardAsTeamInformation() throws IOException{
       List<Team> expected = getExpectedReviewResponse("./expected/boardasteam-expected.json");
       doReturn(new ResponseEntity<>(getExpectedJSON("response/boardsresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("jira"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
       List<Team> actual = featureCollectorTask.updateTeamInformation(featureCollector);

        assertEquals(expected, actual);
    }
    @Test
    public void updateTeamAsBoardInformation() throws IOException{
        doReturn(new ResponseEntity<>(getExpectedJSON("response/boardsresponse-update.json"), HttpStatus.OK)).when(rest).exchange(contains("jira"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<Team> expected = featureCollectorTask.updateTeamInformation(featureCollector);

        assertNotNull(teamRepository.findByName(expected.get(0).getName()));
    }
    @Test
    public void updateTeamAsBoardType() throws IOException{
        doReturn(new ResponseEntity<>(getExpectedJSON("response/boardsresponse-update-1.json"), HttpStatus.OK)).when(rest).exchange(contains("jira"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<Team> expected = featureCollectorTask.updateTeamInformation(featureCollector);

        assertEquals(TEAM_TYPE_SCRUM,teamRepository.findByTeamId(expected.get(0).getTeamId()).getTeamType());
    }
    @Test
    public void addProjectInformation() throws IOException{
        Set<Scope> expected = getExpectedScopeResponse("./expected/scope-expected.json");
        doReturn(new ResponseEntity<>(getExpectedJSON("response/projectresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("jira"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        featureCollectorTask.updateProjectInformation(featureCollector);
        List<Scope> actual = projectRepository.getScopeById("13700");
        assertThat(actual.toArray()[0]).isEqualToIgnoringGivenFields(expected.toArray()[0],"id");
    }
    @Test
    public void updateProjectName() throws IOException{
        Set<Scope> expected = getExpectedScopeResponse("./expected/scope-update-expected.json");
        doReturn(new ResponseEntity<>(getExpectedJSON("response/projectresponse-update.json"), HttpStatus.OK)).when(rest).exchange(contains("jira"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));

        featureCollectorTask.updateProjectInformation(featureCollector);
        List<Scope> actual = projectRepository.getScopeById("137001");
        assertThat(actual.toArray()[0]).isEqualToIgnoringGivenFields(expected.toArray()[0],"id");

    }

    @Test
    public void addStoryInformation() throws IOException{
        List<Feature> expected = getExpectedFeature("./expected/feature-epic-expected.json");
        String epicId = expected.get(0).getsEpicID();
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-empty.json"), HttpStatus.OK)).when(rest).exchange(contains("jira"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/epicresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("/rest/agile/1.0/issue/"+epicId), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-story.json"), HttpStatus.OK)).when(rest).exchange(contains("board/999"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        when(featureBoardRepository.findFeatureBoard(new ObjectId(),"123455")).thenReturn(null);
        featureCollectorTask.updateStoryInformation(featureCollector);
        List<Feature> actual = featureRepository.getStoryByTeamID(expected.get(0).getsTeamID());

        assertThat(actual.toArray()[0]).isEqualToIgnoringGivenFields(expected.toArray()[0],"id", "issueLinks","sSprintUrl");
        assertThat( actual.get(0).getIssueLinks().toArray()[0]).isEqualToIgnoringGivenFields( expected.get(0).getIssueLinks().toArray()[0],"id");

    }
    @Test
    public void addStoryInformationTypeAll() throws IOException{

        List<Feature> expected = getExpectedFeature("./expected/feature-epic-expected.json");
        String epicId = expected.get(0).getsEpicID();
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-empty.json"), HttpStatus.OK)).when(rest).exchange(contains("jira"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/epicresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("/rest/agile/1.0/issue/"+epicId), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-combo.json"), HttpStatus.OK)).when(rest).exchange(contains("board/999"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        when(featureBoardRepository.findFeatureBoard(new ObjectId(),"123455")).thenReturn(null);
        featureCollectorTask.updateStoryInformation(featureCollector);
        List<Feature> actual = featureRepository.getStoryByTeamID(expected.get(0).getsTeamID());

        assertThat(actual.toArray()[0]).isEqualToIgnoringGivenFields(expected.toArray()[0],"id", "issueLinks","sSprintUrl");

    }
    @Test
    public void updateFeatureEpicName() throws IOException{

        List<Feature> expected = getExpectedFeature("./expected/feature-epic-expected.json");
        String epicId = expected.get(0).getsEpicID();
        expected.get(0).setsEpicName("Update Epic Name");
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-empty.json"), HttpStatus.OK)).when(rest).exchange(contains("jira"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/epicresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("/rest/agile/1.0/issue/"+epicId), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-combo-update.json"), HttpStatus.OK)).when(rest).exchange(contains("board/999"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        when(featureBoardRepository.findFeatureBoard(new ObjectId(),"123455")).thenReturn(null);
        featureCollectorTask.updateStoryInformation(featureCollector);
        List<Feature> actual = featureRepository.getStoryByTeamID(expected.get(0).getsTeamID());

        assertThat(actual.toArray()[0]).isEqualToIgnoringGivenFields(expected.toArray()[0],"id", "issueLinks","sSprintUrl");

    }
    @Test
    public void updateEpicBeginDate() throws IOException{

        List<Feature> expected = getExpectedFeature("./expected/feature-epic-expected.json");
        String epicId = expected.get(0).getsEpicID();
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-empty.json"), HttpStatus.OK)).when(rest).exchange(contains("jira"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/epicresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("/rest/agile/1.0/issue/"+epicId), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-combo-update-1.json"), HttpStatus.OK)).when(rest).exchange(contains("board/999"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        when(featureBoardRepository.findFeatureBoard(new ObjectId(),"123455")).thenReturn(null);
        featureCollectorTask.updateStoryInformation(featureCollector);
        List<Feature> actual = featureRepository.getStoryByTeamID(expected.get(0).getsTeamID());

        assertThat(actual.toArray()[0]).isEqualToIgnoringGivenFields(expected.toArray()[0],"id", "issueLinks","sSprintUrl");

    }

    private String getExpectedJSON(String fileName) throws IOException {
        String path = "./" + fileName;
        URL fileUrl = Resources.getResource(path);
        return IOUtils.toString(fileUrl);
    }

    private List<Team> getExpectedReviewResponse (String fileName) throws IOException {
        Gson gson = GsonUtil.getGson();
        return gson.fromJson(getExpectedJSON(fileName), new TypeToken<List<Team>>(){}.getType());
    }
    private Set<Scope> getExpectedScopeResponse (String fileName) throws IOException {
        Gson gson = GsonUtil.getGson();
        return gson.fromJson(getExpectedJSON(fileName), new TypeToken<Set<Scope>>(){}.getType());
    }
    private List<Feature> getExpectedFeature (String fileName) throws IOException {
        Gson gson = GsonUtil.getGson();
        return gson.fromJson(getExpectedJSON(fileName), new TypeToken<List<Feature>>(){}.getType());
    }
}
