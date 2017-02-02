package com.capitalone.dashboard.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyCollection;
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
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.model.UpdateResult;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.IssueItemRepository;
import com.capitalone.dashboard.repository.ProjectItemRepository;
import com.capitalone.dashboard.repository.TeamItemRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFeatureDataClientTest {
	
	@Mock
	private FeatureCollectorRepository featureCollectorRepo;
	
	@Mock
	private TeamItemRepository teamRepo;
	
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
	
	@InjectMocks
	private DefaultFeatureDataClient featureDataClient;
	
	private ObjectId collectorId;
	
	@Before
	public void setup() {
		collectorId = new ObjectId();
		when(featureCollectorRepo.findByName(FeatureCollectorConstants.GITLAB)).thenReturn(collector);
		when(collector.getId()).thenReturn(collectorId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldAddOneTeam() {
		GitlabTeam team1 = new GitlabTeam();
		team1.setId(1L);
		List<GitlabTeam> gitlabTeams = Lists.newArrayList(team1);
		when(teamRepo.getTeamIdById("1")).thenReturn(new ArrayList<>());
		ScopeOwnerCollectorItem scopeOwner = new ScopeOwnerCollectorItem();
		when(featureDataMapper.mapToScopeOwnerCollectorItem(team1, null, collectorId)).thenReturn(scopeOwner);
		when(teamRepo.findByCollectorIdIn(anyCollection())).thenReturn(new ArrayList<>());
		
		UpdateResult result = featureDataClient.updateTeams(gitlabTeams);
		
		assertEquals(1, result.getItemsAdded());
		assertEquals(0, result.getItemsDeleted());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteOneTeamAndAddOneTeam() {
		GitlabTeam team1 = new GitlabTeam();
		team1.setId(1L);
		List<GitlabTeam> gitlabTeams = Lists.newArrayList(team1);
		when(teamRepo.getTeamIdById("1")).thenReturn(Lists.newArrayList(new ScopeOwnerCollectorItem(), new ScopeOwnerCollectorItem()));
		ScopeOwnerCollectorItem scopeOwner = new ScopeOwnerCollectorItem();
		scopeOwner.setTeamId("1");
		when(featureDataMapper.mapToScopeOwnerCollectorItem(team1, null, collectorId)).thenReturn(scopeOwner);
		ScopeOwnerCollectorItem savedTeam = new ScopeOwnerCollectorItem();
		savedTeam.setTeamId("3");
		List<ScopeOwnerCollectorItem> savedTeams = Lists.newArrayList(savedTeam);
		when(teamRepo.findByCollectorIdIn(anyCollection())).thenReturn(savedTeams);
		
		UpdateResult result = featureDataClient.updateTeams(gitlabTeams);
		
		assertEquals(1, result.getItemsAdded());
		assertEquals(1, result.getItemsDeleted());
	}
	
	@Test
	public void shouldUpdateProjects() {
		GitlabProject gitlabProject = new GitlabProject();
		gitlabProject.setId(1L);
		List<GitlabProject> projects = Lists.newArrayList(gitlabProject);
		Scope savedProject = new Scope();
		savedProject.setpId("3");
		List<Scope> savedProjects = Lists.newArrayList(savedProject);
		when(projectRepo.findScopeByCollectorId(collectorId)).thenReturn(savedProjects);
		Scope scope = new Scope();
		scope.setpId("1");
		when(featureDataMapper.mapToScopeItem(gitlabProject, null, collectorId)).thenReturn(scope);
		
		UpdateResult result = featureDataClient.updateProjects(projects);
		
		assertEquals(1, result.getItemsDeleted());
		assertEquals(1, result.getItemsAdded());
	}
	
	@Test
	public void shouldUpdateIssues() {
		String projectId = "7";
		GitlabIssue gitlabIssue = new GitlabIssue();
		gitlabIssue.setId(1L);
		List<String> labels = Lists.newArrayList("To Do");
		gitlabIssue.setLabels(labels);
		List<GitlabIssue> issues = Lists.newArrayList(gitlabIssue);
		GitlabLabel gitlabLabel = new GitlabLabel();
		gitlabLabel.setName("To Do");
		List<GitlabLabel> inProgressLabelsForProject = Lists.newArrayList(gitlabLabel);
		Feature savedFeature = new Feature();
		savedFeature.setsId("6");
		savedFeature.setCollectorId(collectorId);
		List<Feature> savedFeatures = Lists.newArrayList(savedFeature );
		when(issueItemRepo.getFeaturesByCollectorAndProjectId(collectorId, projectId)).thenReturn(savedFeatures );
		
		UpdateResult result = featureDataClient.updateIssues(projectId , issues , inProgressLabelsForProject );
		
		assertEquals(1, result.getItemsDeleted());
		assertEquals(1, result.getItemsAdded());
	}
	
	@Test
	public void shouldFindEnabledTeams() {
		featureDataClient.findEnabledTeams(collectorId);
		
		verify(teamRepo, times(1)).findEnabledTeams(collectorId);
	}

}
