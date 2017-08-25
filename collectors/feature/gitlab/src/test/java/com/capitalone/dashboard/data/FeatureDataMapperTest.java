package com.capitalone.dashboard.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabMilestone;
import com.capitalone.dashboard.gitlab.model.GitlabNamespace;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.Project;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.google.common.collect.Lists;

public class FeatureDataMapperTest {
	
	private FeatureDataMapper mapper = new FeatureDataMapper();

	
	@Test
	public void shouldMapInProgressKanbanIssue() {
	    Project project = new Project("capitalone", "hygieia");
		List<String> labels = Lists.newArrayList("Doing");
		GitlabProject gitlabProject = new GitlabProject();
		GitlabNamespace namespace = new GitlabNamespace();
		namespace.setId(23L);
		gitlabProject.setNamespace(namespace);
		GitlabIssue issue = new GitlabIssue();
		issue.setLabels(labels );
		List<String> inProgressLabelsForProject = Lists.newArrayList("Doing", "ToDo");
		ObjectId existingIssueId = new ObjectId();
		ObjectId gitlabCollectorId = new ObjectId();
		
		Feature result = mapper.mapToFeatureItem(project, issue , inProgressLabelsForProject , existingIssueId, gitlabCollectorId);
		
		assertNotNull(result);
		assertEquals("In Progress", result.getsStatus());
		assertEquals(FeatureCollectorConstants.SPRINT_KANBAN, result.getsSprintID());
	}
	
	@Test
	public void shouldMapDoneScrumIssue() {	
	    Project project = new Project("capitalone", "hygieia");
		List<String> labels = Lists.newArrayList("");
		GitlabProject gitlabProject = new GitlabProject();
		GitlabNamespace namespace = new GitlabNamespace();
		namespace.setId(23L);
		gitlabProject.setNamespace(namespace);
		GitlabMilestone milestone = new GitlabMilestone();
		milestone.setId(23L);
		milestone.setDueDate("date");
		GitlabIssue issue = new GitlabIssue();
		issue.setState("closed");
		issue.setLabels(labels);
		issue.setMilestone(milestone);
		List<String> inProgressLabelsForProject = Lists.newArrayList("Doing", "ToDo");
		ObjectId existingIssueId = new ObjectId();
		ObjectId gitlabCollectorId = new ObjectId();
		
		Feature result = mapper.mapToFeatureItem(project, issue , inProgressLabelsForProject , existingIssueId, gitlabCollectorId);
		
		assertNotNull(result);
		assertEquals("Done", result.getsStatus());
		assertEquals("23", result.getsSprintID());
	}
	
	@Test
	public void shouldMapUnknownKanbanIssue() {	
	    Project project = new Project("capitalone", "hygieia");
		List<String> labels = Lists.newArrayList("");
		GitlabProject gitlabProject = new GitlabProject();
		GitlabNamespace namespace = new GitlabNamespace();
		namespace.setId(23L);
		gitlabProject.setNamespace(namespace);
		GitlabMilestone milestone = new GitlabMilestone();
		milestone.setId(23L);
		GitlabIssue issue = new GitlabIssue();
		issue.setLabels(labels);
		issue.setMilestone(milestone);
		List<String> inProgressLabelsForProject = Lists.newArrayList("Doing", "ToDo");
		ObjectId existingIssueId = new ObjectId();
		ObjectId gitlabCollectorId = new ObjectId();
		
		Feature result = mapper.mapToFeatureItem(project, issue , inProgressLabelsForProject , existingIssueId, gitlabCollectorId);
		
		assertNotNull(result);
		assertEquals("", result.getsStatus());
		assertEquals(FeatureCollectorConstants.SPRINT_KANBAN, result.getsSprintID());
	}

}
