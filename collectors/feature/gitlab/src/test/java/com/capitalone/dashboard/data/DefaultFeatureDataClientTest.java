package com.capitalone.dashboard.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabMilestone;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.model.UpdateResult;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.IssueItemRepository;
import com.capitalone.dashboard.repository.ProjectItemRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import com.capitalone.dashboard.repository.WidgetRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFeatureDataClientTest {
	
	@Mock
	private FeatureCollectorRepository featureCollectorRepo;
	
	@Mock
	private TeamRepository teamRepo;
	
	@Mock
	private ProjectItemRepository projectRepo;
	
	@Mock
	private IssueItemRepository issueItemRepo;
	
	@Mock
	private FeatureRepository featureRepo;
	
	@Mock
	private FeatureDataMapper featureDataMapper;
	
	@Mock
	private FeatureCollector collector;
	
	@Mock
	private WidgetRepository widgetRepo;
	
	@InjectMocks
	private DefaultFeatureDataClient featureDataClient;
	
	private ObjectId collectorId;
	
	@Before
	public void setup() {
		collectorId = new ObjectId();
		when(featureCollectorRepo.findByName(FeatureCollectorConstants.GITLAB)).thenReturn(collector);
		when(collector.getId()).thenReturn(collectorId);
	}

	@Test
	public void shouldAddOneTeam() {
		GitlabTeam team1 = new GitlabTeam();
		team1.setId(1L);
		List<GitlabTeam> gitlabTeams = Lists.newArrayList(team1);
		when(teamRepo.findByTeamId("1")).thenReturn(null);
		Team team = new Team("id", "name");
		when(featureDataMapper.mapToTeam(team1, null, collectorId)).thenReturn(team);
		when(teamRepo.findByCollectorId(isA(ObjectId.class))).thenReturn(new ArrayList<>());
		
		UpdateResult result = featureDataClient.updateTeams(collectorId, gitlabTeams);
		
		assertEquals(1, result.getItemsAdded());
		assertEquals(0, result.getItemsDeleted());
	}
	
	@Test
	public void shouldDeleteOneTeamAndAddOneTeam() {
		GitlabTeam team1 = new GitlabTeam();
		team1.setId(1L);
		List<GitlabTeam> gitlabTeams = Lists.newArrayList(team1);
		when(teamRepo.findByTeamId("1")).thenReturn(new Team("1", "name"));
		Team team = new Team("id", "name");
		when(featureDataMapper.mapToTeam(team1, null, collectorId)).thenReturn(team);
		Team savedTeam = new Team("3", "name");
		List<Team> savedTeams = Lists.newArrayList(savedTeam);
		when(teamRepo.findByCollectorId(isA(ObjectId.class))).thenReturn(savedTeams);
		
		UpdateResult result = featureDataClient.updateTeams(collectorId, gitlabTeams);
		
		assertEquals(1, result.getItemsAdded());
		assertEquals(1, result.getItemsDeleted());
	}
	
	@Test
	public void shouldUpdateProjects() {
		GitlabProject gitlabProject = new GitlabProject();
		gitlabProject.setId(3L);
		List<GitlabProject> projects = Lists.newArrayList(gitlabProject);
		Scope savedProject = new Scope();
		savedProject.setpId("3");
		savedProject.setCollectorId(collectorId);
		List<Scope> savedProjects = Lists.newArrayList(savedProject);
		when(projectRepo.getScopeIdById("3")).thenReturn(Lists.newArrayList(savedProject));
		when(projectRepo.findScopeByCollectorId(collectorId)).thenReturn(savedProjects);
		Scope scope = new Scope();
		scope.setpId("3");
		when(featureDataMapper.mapToScopeItem(gitlabProject, null, collectorId)).thenReturn(scope);
		
		UpdateResult result = featureDataClient.updateProjects(collectorId, projects);
		
		assertEquals(1, result.getItemsDeleted());
		assertEquals(1, result.getItemsAdded());
	}
	
	@Test
	public void shouldUpdateIssues() {
		String projectId = "7";
		
		GitlabIssue gitlabIssue = new GitlabIssue();
		gitlabIssue.setId(1L);
		gitlabIssue.setUpdatedAt("2018-02-20T12:00:00+01:00");
		List<String> labels = Lists.newArrayList("To Do");
		gitlabIssue.setLabels(labels);
		
		GitlabIssue gitlabIssue2 = new GitlabIssue();
        gitlabIssue2.setId(1L);
        gitlabIssue2.setUpdatedAt("2016-02-20T12:00:00+01:00");
        gitlabIssue2.setLabels(labels);
        GitlabMilestone milestone = new GitlabMilestone();
        milestone.setUpdatedAt("2018-02-20T12:00:00+01:00");
        gitlabIssue2.setMilestone(milestone);
		
		List<GitlabIssue> issues = Lists.newArrayList(gitlabIssue, gitlabIssue2);
		GitlabLabel gitlabLabel = new GitlabLabel();
		gitlabLabel.setName("To Do");
		List<GitlabLabel> inProgressLabelsForProject = Lists.newArrayList(gitlabLabel);
		Feature savedFeature = new Feature();
		savedFeature.setsId("6");
		savedFeature.setCollectorId(collectorId);
		List<Feature> savedFeatures = Lists.newArrayList(savedFeature );
		when(issueItemRepo.getFeaturesByCollectorAndProjectId(collectorId, projectId)).thenReturn(savedFeatures );
		
		UpdateResult result = featureDataClient.updateIssues(collectorId, 1487688565442L, projectId , issues , inProgressLabelsForProject );
		
		assertEquals(1, result.getItemsDeleted());
		assertEquals(2, result.getItemsAdded());
	}
	
	@Test
    public void shouldNotUpdateIssues() {
        String projectId = "7";
        
        GitlabIssue gitlabIssue = new GitlabIssue();
        gitlabIssue.setId(1L);
        gitlabIssue.setUpdatedAt("2016-02-20T12:00:00+01:00");
        List<String> labels = Lists.newArrayList("To Do");
        gitlabIssue.setLabels(labels);
        
        GitlabIssue gitlabIssue2 = new GitlabIssue();
        gitlabIssue2.setId(1L);
        gitlabIssue2.setUpdatedAt("2016-02-20T12:00:00+01:00");
        gitlabIssue2.setLabels(labels);
        GitlabMilestone milestone = new GitlabMilestone();
        milestone.setUpdatedAt("2016-02-20T12:00:00+01:00");
        gitlabIssue2.setMilestone(milestone);
        
        List<GitlabIssue> issues = Lists.newArrayList(gitlabIssue, gitlabIssue2);
        GitlabLabel gitlabLabel = new GitlabLabel();
        gitlabLabel.setName("To Do");
        List<GitlabLabel> inProgressLabelsForProject = Lists.newArrayList(gitlabLabel);
        Feature savedFeature = new Feature();
        savedFeature.setsId("6");
        savedFeature.setCollectorId(collectorId);
        List<Feature> savedFeatures = Lists.newArrayList(savedFeature );
        when(issueItemRepo.getFeaturesByCollectorAndProjectId(collectorId, projectId)).thenReturn(savedFeatures );
        
        UpdateResult result = featureDataClient.updateIssues(collectorId, 1487688565442L, projectId , issues , inProgressLabelsForProject );
        
        assertEquals(1, result.getItemsDeleted());
        assertEquals(0, result.getItemsAdded());
    }
	
	@Test
	public void shouldFindEnabledTeams() {
		featureDataClient.getEnabledWidgets(collectorId);
		
		verify(widgetRepo, times(1)).findByCollectorIdAndEnabled(collectorId, true);
	}

}
