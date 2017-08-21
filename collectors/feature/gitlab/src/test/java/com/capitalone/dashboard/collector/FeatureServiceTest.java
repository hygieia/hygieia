package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
import com.capitalone.dashboard.gitlab.model.GitlabNamespace;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Project;
import com.capitalone.dashboard.model.UpdateResult;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class FeatureServiceTest {
	
	@Mock
	private GitlabClient gitlabClient;
	
	@Mock
	private FeatureDataClient dataClient;
	
	@InjectMocks
	private FeatureService service;

	@Test
	public void shouldGetProjectsToUpdateWithProjectName() {
	    String teamId = "capitalone";
	    String projectId = "hygieia";
		ObjectId id = new ObjectId();
		
		List<CollectorItem> enabledWidgets = new ArrayList<>();
		CollectorItem item1 = new CollectorItem();
		item1.getOptions().put("teamId", teamId);
		item1.getOptions().put("projectId", projectId);

        enabledWidgets.add(item1);
        when(dataClient.getEnabledWidgets(id)).thenReturn(enabledWidgets);

		Collection<Project> projects = service.getProjectsToUpdate(id);
		
		assertEquals(1, projects.size());
		projects.contains(new Project(teamId, projectId));
	}
	
	@Test
    public void shouldGetProjectsToUpdateWithoutProjectName() {
        String teamId = "capitalone";
        String projectId = "hygieia";
        ObjectId id = new ObjectId();
        
        List<CollectorItem> enabledWidgets = new ArrayList<>();
        CollectorItem item1 = new CollectorItem();
        item1.getOptions().put("teamId", teamId);

        enabledWidgets.add(item1);
        when(dataClient.getEnabledWidgets(id)).thenReturn(enabledWidgets);
        
        GitlabProject project = new GitlabProject();
        project.setName(projectId);
        project.setNamespace(new GitlabNamespace());
        project.getNamespace().setName(teamId);
        List<GitlabProject> gitlabProjects = Lists.newArrayList();
        gitlabProjects.add(project);
        when(gitlabClient.getProjectsForTeam(teamId)).thenReturn(gitlabProjects);

        Collection<Project> projects = service.getProjectsToUpdate(id);
        
        assertEquals(1, projects.size());
        projects.contains(new Project(teamId, projectId));
    }
	
	@Test
	public void shouldUpdateIssuesForProject() throws InterruptedException, ExecutionException {
	    ObjectId id = new ObjectId();
	    Collector collector = new Collector();
	    collector.setId(id);
	   
		Project project = new Project("capitalone", "hygieia");
		List<GitlabLabel> labels = new ArrayList<>();
		when(gitlabClient.getInProgressLabelsForProject(project)).thenReturn(labels);
		ArrayList<GitlabIssue> issues = new ArrayList<>();
		when(gitlabClient.getIssuesForProject(project)).thenReturn(issues);
		when(dataClient.updateIssues(collector, project, issues, labels)).thenReturn(new UpdateResult(2, 1));
		
		Future<UpdateResult> result = service.updateIssuesForProject(collector, project);
		assertEquals(2, result.get().getItemsAdded());
		assertEquals(1, result.get().getItemsDeleted());
        assertNotNull(result);
		
		verify(dataClient).updateIssues(collector, project, issues, labels);
	}

}
