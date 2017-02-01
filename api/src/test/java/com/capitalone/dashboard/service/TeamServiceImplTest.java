package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceImplTest {
    private static Team mockV1Team;
    private static Team mockJiraTeam;
    private static Team mockJiraTeam2;

    private static Component mockComponent;
    private static Collector mockV1Collector;
    private static Collector mockJiraCollector;
    private static CollectorItem mockItem;
    private static CollectorItem mockItem2;
    private static CollectorItem mockItem3;
    private static CollectorItem mockItem4;
    private static final String generalUseDate = "2015-11-01T00:00:00.000-00:00";
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000-00:00");
    private static Calendar cal = Calendar.getInstance();
    private static final String maxDateWinner = DatatypeConverter
            .printDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
    private static String maxDateLoser = new String();
    private static String currentSprintEndDate = new String();
    private static final ObjectId jiraCollectorId = new ObjectId();
    private static final ObjectId v1CollectorId = new ObjectId();
    private static final ObjectId mockComponentId = new ObjectId();

    @Mock
    ComponentRepository componentRepository;
    @Mock
    TeamRepository teamRepository;
    @Mock
    private CollectorRepository collectorRepository;
    @InjectMocks
    TeamServiceImpl teamService;

    @Before
    public void setup() {
        // Date-time modifications
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        maxDateLoser = df.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, +13);
        currentSprintEndDate = df.format(cal.getTime());

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

        mockComponent = new Component();
        mockComponent.getCollectorItems().put(CollectorType.AgileTool,
                Arrays.asList(mockItem2, mockItem3, mockItem4));
        mockComponent.setId(mockComponentId);
        mockComponent.setName("Feature Widget Test");
        mockComponent.setOwner("kfk884");

        // Saving to mock repos
        componentRepository.save(mockComponent);
        collectorRepository.save(mockJiraCollector);
        collectorRepository.save(mockV1Collector);
        teamRepository.save(mockV1Team);
        teamRepository.save(mockJiraTeam);
        teamRepository.save(mockJiraTeam2);
    }

    @After
    public void cleanup() {
        mockV1Team = null;
        mockJiraTeam = null;
        mockJiraTeam2 = null;
        mockV1Collector = null;
        mockItem = null;
        mockItem2 = null;
        mockItem3 = null;
        mockItem4 = null;
        mockComponent = null;
    }

    @Test
    public void testGetTeam() {
        when(componentRepository.findOne(mockComponentId)).thenReturn(mockComponent);
        when(collectorRepository.findOne(mockItem2.getCollectorId())).thenReturn(mockJiraCollector);
        when(teamRepository.findByTeamId(Mockito.anyString()))
                .thenReturn(mockJiraTeam);

        DataResponse<Team> result = teamService.getTeam(mockComponentId,
                mockJiraTeam.getTeamId());
        assertEquals(result.getResult(), mockJiraTeam);
    }


}