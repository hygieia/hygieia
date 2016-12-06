package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.data.FeatureDataClient;
import com.capitalone.dashboard.gitlab.GitlabClient;
import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.model.UpdateResult;

@RunWith(MockitoJUnitRunner.class)
public class FeatureServiceTest {
	
	@Mock
	private GitlabClient gitlabClient;
	
	@Mock
	private FeatureDataClient dataClient;
	
	@InjectMocks
	private FeatureService service;

	@Test
	public void shouldGetProjectsForEnabledTeams() {
		ObjectId id = new ObjectId();
		List<ScopeOwnerCollectorItem> items = new ArrayList<>();
		ScopeOwnerCollectorItem team1 = new ScopeOwnerCollectorItem();
		team1.setTeamId("team1");
		ScopeOwnerCollectorItem team2 = new ScopeOwnerCollectorItem();
		team2.setTeamId("team2");
		items.add(team1);
		items.add(team2);
		when(dataClient.findEnabledTeams(id)).thenReturn(items);
		
		List<GitlabProject> projects1 = new ArrayList<>();
		List<GitlabProject> projects2 = new ArrayList<>();
		projects1.add(new GitlabProject());
		projects1.add(new GitlabProject());
		projects2.add(new GitlabProject());
		when(gitlabClient.getProjects(team1)).thenReturn(projects1);
		when(gitlabClient.getProjects(team2)).thenReturn(projects2);
		
		List<GitlabProject> projects = service.getProjectsForEnabledTeams(id);
		
		assertEquals(3, projects.size());
		assertTrue(projects.containsAll(projects1));
		assertTrue(projects.containsAll(projects2));
	}
	
	@Test
	public void shouldUpdateSelectableTeams() {
		List<GitlabTeam> teams = new ArrayList<>();
		when(gitlabClient.getTeams()).thenReturn(teams);
		when(dataClient.updateTeams(teams)).thenReturn(new UpdateResult(2, 1));
		
		assertNotNull(service.updateSelectableTeams());
		
		verify(gitlabClient).getTeams();
		verify(dataClient).updateTeams(teams);
	}
	
	@Test
	public void shouldUpdateProjects() {
		List<GitlabProject> projects = new ArrayList<>();
		when(dataClient.updateProjects(projects)).thenReturn(new UpdateResult(2, 1));
		
		assertNotNull(service.updateProjects(projects));
		
		verify(dataClient).updateProjects(projects);
	}
	
	@Test
	public void shouldUpdateIssuesForProject() {
		GitlabProject project = new GitlabProject();
		Long projectId = 23L;
		project.setId(projectId);
		List<GitlabLabel> labels = new ArrayList<>();
		when(gitlabClient.getInProgressLabelsForProject(projectId)).thenReturn(labels);
		ArrayList<GitlabIssue> issues = new ArrayList<>();
		when(gitlabClient.getIssuesForProject(project)).thenReturn(issues);
		when(dataClient.updateIssues(String.valueOf(projectId), issues, labels)).thenReturn(new UpdateResult(2, 1));
		
		assertNotNull(service.updateIssuesForProject(project));
		
		verify(dataClient).updateIssues(String.valueOf(projectId), issues, labels);
	}

}
