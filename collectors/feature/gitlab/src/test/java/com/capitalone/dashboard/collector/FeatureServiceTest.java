package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
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
import com.capitalone.dashboard.model.CollectorItem;
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
	public void shouldGetEnabledProjects() {
		ObjectId id = new ObjectId();
		
		List<CollectorItem> enabledWidgets = new ArrayList<>();
		CollectorItem item1 = new CollectorItem();
		item1.getOptions().put("teamId", "213213");
		item1.getOptions().put("projectId", "209");
		CollectorItem item2 = new CollectorItem();
        item2.getOptions().put("teamId", "Any");
        item2.getOptions().put("projectId", "309");
        enabledWidgets.add(item1);
        enabledWidgets.add(item2);
        when(dataClient.getEnabledWidgets(id)).thenReturn(enabledWidgets);

		List<GitlabProject> projects1 = new ArrayList<>();
		projects1.add(new GitlabProject());
		projects1.add(new GitlabProject());
		when(gitlabClient.getProjectsForTeam("213213")).thenReturn(projects1);
		when(gitlabClient.getProjectById("309")).thenReturn(new GitlabProject());
		
		Collection<GitlabProject> projects = service.getEnabledProjects(id);
		
		assertEquals(1, projects.size());
		assertTrue(projects.containsAll(projects1));
	}
	
	@Test
	public void shouldUpdateSelectableTeams() {
	    ObjectId id = new ObjectId();
		List<GitlabTeam> teams = new ArrayList<>();
		when(gitlabClient.getTeams()).thenReturn(teams);
		when(dataClient.updateTeams(id, teams)).thenReturn(new UpdateResult(2, 1));
		
		assertNotNull(service.updateSelectableTeams(id));
		
		verify(gitlabClient).getTeams();
		verify(dataClient).updateTeams(id, teams);
	}
	
	@Test
	public void shouldUpdateProjects() {
	    ObjectId id = new ObjectId();
		List<GitlabProject> projects = new ArrayList<>();
		when(dataClient.updateProjects(id, projects)).thenReturn(new UpdateResult(2, 1));
		
		assertNotNull(service.updateProjects(id));
		
		verify(dataClient).updateProjects(id, projects);
	}
	
	@Test
	public void shouldUpdateIssuesForProject() {
	    ObjectId id = new ObjectId();
		GitlabProject project = new GitlabProject();
		Long projectId = 23L;
		project.setId(projectId);
		List<GitlabLabel> labels = new ArrayList<>();
		when(gitlabClient.getInProgressLabelsForProject(projectId)).thenReturn(labels);
		ArrayList<GitlabIssue> issues = new ArrayList<>();
		when(gitlabClient.getIssuesForProject(project)).thenReturn(issues);
		when(dataClient.updateIssues(id, projectId, String.valueOf(projectId), issues, labels)).thenReturn(new UpdateResult(2, 1));
		
		assertNotNull(service.updateIssuesForProject(id, projectId, project));
		
		verify(dataClient).updateIssues(id, projectId, String.valueOf(projectId), issues, labels);
	}

}
