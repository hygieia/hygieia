package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.config.FongoConfig;
import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.model.FeatureEpicResult;
import com.capitalone.dashboard.model.JiraMode;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.util.Supplier;
import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
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
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, FongoConfig.class})
@DirtiesContext
public class DefaultJiraClientTest {
    @Mock
    private DefaultJiraClient defaultJiraClient;
    @Mock
    private Supplier<RestOperations> restOperationsSupplier = mock(Supplier.class);
    @Mock
    private RestOperations rest = mock(RestOperations.class);
    @Autowired
    private FeatureSettings featureSettings;

    @Before
    public void loadStuff() {
        when(restOperationsSupplier.get()).thenReturn(rest);

        defaultJiraClient = new DefaultJiraClient(featureSettings,restOperationsSupplier);
    }

    @Test
    public void getIssuesBoard() throws IOException{
        Team team = new Team("123","testTeam");
        doReturn(new ResponseEntity<>(getExpectedJSON("response/epicresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("rest/agile/1.0/issue/"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));

        doReturn(new ResponseEntity<>(getExpectedJSON("response/issueresponse-combo.json"), HttpStatus.OK)).when(rest).exchange(contains("rest/agile/1.0/board/"+team.getTeamId()), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        Map<String, String> issueTypeIds = new HashMap<>();
        issueTypeIds.put("Epic", "6");
        issueTypeIds.put("Story", "7");
        FeatureEpicResult featureEpicResult = defaultJiraClient.getIssues(team, issueTypeIds);
        assertThat(featureEpicResult.getEpicList().size()).isEqualTo(1);
        assertThat(featureEpicResult.getFeatureList().size()).isEqualTo(1);
    }
    @Test
    public void getProjectsWithAuth() throws IOException{
        doReturn(new ResponseEntity<>(getExpectedJSON("response/projectresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("api/2/project"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        featureSettings.setJiraCredentials("dXNlcm5hbWU6cGFzc3dvcmQ=");
        defaultJiraClient = new DefaultJiraClient(featureSettings,restOperationsSupplier);
        Set<Scope> projects = defaultJiraClient.getProjects();
        assertThat(projects.stream().count()).isEqualTo(2);
    }
    @Test
    public void getProjects() throws IOException{
        doReturn(new ResponseEntity<>(getExpectedJSON("response/projectresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("api/2/project"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        Set<Scope> projects = defaultJiraClient.getProjects();
        assertThat(projects.stream().count()).isEqualTo(2);
    }
    @Test
    public void getProjectsParseException(){
        doReturn(new ResponseEntity<>("{}", HttpStatus.OK)).when(rest).exchange(contains("api/2/project"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        ParseException excep = new ParseException("org.json.simple.JSONObject cannot be cast to org.json.simple.JSONArray",0);

        try {
            defaultJiraClient.getProjects();
            fail("Should throw ParseException");
        } catch(Exception e) {
            assertEquals(excep.getMessage(), e.getMessage());
        }
    }

    @Test
    public void getBoards() throws IOException{
        doReturn(new ResponseEntity<>(getExpectedJSON("response/boardsresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("/rest/agile/1.0/board"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<Team> projects = defaultJiraClient.getBoards();
        assertThat(projects.stream().count()).isEqualTo(4);
    }
    @Test
    public void getAllIssueIds() throws IOException{
        doReturn(new ResponseEntity<>(getExpectedJSON("response/issuerefreshresponse.json"), HttpStatus.OK)).when(rest).exchange(contains("rest/agile/1.0/"), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class));
        List<String> ids = defaultJiraClient.getAllIssueIds("1234", JiraMode.Board);
        assertThat(ids.stream().count()).isEqualTo(7);
    }



    private String getExpectedJSON(String fileName) throws IOException {
        String path = "./" + fileName;
        URL fileUrl = Resources.getResource(path);
        return IOUtils.toString(fileUrl);
    }
}
