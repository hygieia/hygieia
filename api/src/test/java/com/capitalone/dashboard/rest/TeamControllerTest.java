package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.service.TeamService;
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
import java.util.Calendar;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, WebMVCConfig.class })
@WebAppConfiguration
public class TeamControllerTest {
    private static Team mockV1Team;
    private static Team mockJiraTeam;
    private static Team mockJiraTeam2;
    private static Component mockComponent;
    private static Collector mockV1Collector;
    private static Collector mockJiraCollector;
    private static CollectorItem mockItem;
    private static CollectorItem mockItem2;
    private static CollectorItem mockItem3;
    private static CollectorItem mockBadItem;
    private static final String generalUseDate = "2015-11-01T00:00:00Z";
    private static final String olderThanGeneralUseDate = "2015-10-30T00:00:00Z";
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static Calendar cal = Calendar.getInstance();
    private static final String maxDateWinner = df.format(new Date());
    private static String maxDateLoser = new String();
    private static final ObjectId jiraCollectorId = new ObjectId();
    private static final ObjectId v1CollectorId = new ObjectId();
    private static final ObjectId mockComponentId = new ObjectId();

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private TeamService teamService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        // Date-time modifications
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        maxDateLoser = df.format(cal.getTime());

        // VersionOne Mock Scope
        mockV1Team = new Team("", "");
        mockV1Team.setCollectorId(v1CollectorId);
        mockV1Team.setIsDeleted("False");
        mockV1Team.setChangeDate(maxDateLoser);
        mockV1Team.setAssetState("Active");
        mockV1Team.setId(ObjectId.get());
        mockV1Team.setTeamId("Team:129825");
        mockV1Team.setName("Resistance");
        mockV1Team.setEnabled(true);

        // Jira Mock Scope
        // Mock Scope 1
        mockJiraTeam = new Team("", "");
        mockJiraTeam.setCollectorId(jiraCollectorId);
        mockJiraTeam.setIsDeleted("False");
        mockJiraTeam.setChangeDate(maxDateWinner);
        mockJiraTeam.setAssetState("Active");
        mockJiraTeam.setId(ObjectId.get());
        mockJiraTeam.setTeamId("871589423");
        mockJiraTeam.setName("Sith Lords");
        mockJiraTeam.setEnabled(true);

        // Mock Scope 2
        mockJiraTeam2 = new Team("", "");
        mockJiraTeam2.setCollectorId(jiraCollectorId);
        mockJiraTeam2.setIsDeleted("False");
        mockJiraTeam2.setChangeDate(generalUseDate);
        mockJiraTeam2.setAssetState("Active");
        mockJiraTeam2.setId(ObjectId.get());
        mockJiraTeam2.setTeamId("078123416");
        mockJiraTeam2.setName("Jedi Knights");
        mockJiraTeam2.setEnabled(false);

        // Creating Collector and Component relationship artifacts
        mockV1Collector = new Collector();
        mockV1Collector.setCollectorType(CollectorType.AgileTool);
        mockV1Collector.setEnabled(true);
        mockV1Collector.setName("VersionOne Collector");
        mockV1Collector.setOnline(true);
        mockV1Collector.setId(v1CollectorId);

        mockJiraCollector = new Collector();
        mockJiraCollector.setCollectorType(CollectorType.AgileTool);
        mockJiraCollector.setEnabled(true);
        mockJiraCollector.setName("Jira Collector");
        mockJiraCollector.setOnline(true);
        mockJiraCollector.setId(jiraCollectorId);

        mockItem = new CollectorItem();
        mockItem.setId(new ObjectId());
        mockItem.setCollectorId(v1CollectorId);
        mockItem.setEnabled(true);
        mockItem.setCollector(mockV1Collector);

        mockItem2 = new CollectorItem();
        mockItem2.setId(new ObjectId());
        mockItem2.setCollectorId(jiraCollectorId);
        mockItem2.setEnabled(true);
        mockItem2.setCollector(mockJiraCollector);

        mockItem3 = new CollectorItem();
        mockItem3.setId(new ObjectId());
        mockItem3.setCollectorId(jiraCollectorId);
        mockItem3.setEnabled(true);
        mockItem3.setCollector(mockJiraCollector);

        mockComponent = new Component();
        mockComponent.addCollectorItem(CollectorType.AgileTool, mockItem);
        mockComponent.addCollectorItem(CollectorType.AgileTool, mockItem2);
        mockComponent.addCollectorItem(CollectorType.AgileTool, mockItem3);
        mockComponent.setId(mockComponentId);
        mockComponent.setName("Feature Widget Test");
        mockComponent.setOwner("kfk884");
    }

    @After
    public void after() {
        mockV1Team = null;
        mockJiraTeam = null;
        mockJiraTeam2 = null;
        mockV1Collector = null;
        mockItem = null;
        mockComponent = null;
        mockMvc = null;
    }

    @Test
    public void testGetV1Team_HappyPath() throws Exception {
        String testTeamId = mockV1Team.getTeamId();
        DataResponse<Team> response = new DataResponse<>(mockV1Team,
                mockV1Collector.getLastExecuted());

        when(teamService.getTeam(mockComponentId, testTeamId)).thenReturn(response);
        mockMvc.perform(get("/team/" + testTeamId + "?component=" + mockComponentId.toString() ))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetJiraTeam_HappyPath() throws Exception {
        String testTeamId = mockJiraTeam.getTeamId();
        DataResponse<Team> response = new DataResponse<>(mockJiraTeam,
                mockJiraCollector.getLastExecuted());

        when(teamService.getTeam(mockComponentId, testTeamId)).thenReturn(response);
        mockMvc.perform(get("/team/" + testTeamId + "?component=" + mockComponentId.toString() ))
                .andExpect(status().isOk());
    }

}
