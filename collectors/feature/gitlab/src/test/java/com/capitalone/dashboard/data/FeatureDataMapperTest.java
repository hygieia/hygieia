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
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.google.common.collect.Lists;

public class FeatureDataMapperTest {
	
	private FeatureDataMapper mapper = new FeatureDataMapper();

	@Test
	public void shouldMapGitlabTeamToScopeOwnerCollectorItem() {
		GitlabTeam gitlabTeam = new GitlabTeam();
		gitlabTeam.setId(23L);
		gitlabTeam.setName("teamName");
		ObjectId existingTeamId = new ObjectId();
		ObjectId gitlabFeatureCollectorId = new ObjectId();
		
		Team result = mapper.mapToTeam(gitlabTeam , existingTeamId , gitlabFeatureCollectorId );
		
		assertNotNull(result);
		assertEquals(existingTeamId, result.getId());
		assertEquals(gitlabFeatureCollectorId, result.getCollectorId());
		assertEquals("23", result.getTeamId());
		assertEquals("teamName", result.getName());
		assertEquals("", result.getChangeDate());
		assertEquals("Active", result.getAssetState());
		assertEquals("False", result.getIsDeleted());
	}
	
	@Test
	public void shouldMapGitlabProjectToScopeItem() {
		GitlabProject project = new GitlabProject();
		project.setId(23L);
		project.setName("name");
		project.setPath("path");
		ObjectId existingProjectId = new ObjectId();
		ObjectId gitlabFeatureCollectorId = new ObjectId();
		
		Scope result = mapper.mapToScopeItem(project, existingProjectId, gitlabFeatureCollectorId);
	
		assertNotNull(result);
		assertEquals(existingProjectId, result.getId());
		assertEquals(gitlabFeatureCollectorId, result.getCollectorId());
		assertEquals("23", result.getpId());
		assertEquals("name", result.getName());
		assertEquals("", result.getBeginDate());
		assertEquals("", result.getEndDate());
		assertEquals("", result.getChangeDate());
		assertEquals("Active", result.getAssetState());
		assertEquals("False", result.getIsDeleted());
		assertEquals("path", result.getProjectPath());
	}
	
	@Test
	public void shouldMapInProgressKanbanIssue() {	
		List<String> labels = Lists.newArrayList("Doing");
		GitlabProject project = new GitlabProject();
		GitlabNamespace namespace = new GitlabNamespace();
		namespace.setId(23L);
		project.setNamespace(namespace);
		GitlabIssue issue = new GitlabIssue();
		issue.setLabels(labels );
		issue.setProject(project);
		List<String> inProgressLabelsForProject = Lists.newArrayList("Doing", "ToDo");
		ObjectId existingIssueId = new ObjectId();
		ObjectId gitlabCollectorId = new ObjectId();
		
		Feature result = mapper.mapToFeatureItem(issue , inProgressLabelsForProject , existingIssueId, gitlabCollectorId);
		
		assertNotNull(result);
		assertEquals("In Progress", result.getsStatus());
		assertEquals(FeatureCollectorConstants.SPRINT_KANBAN, result.getsSprintID());
	}
	
	@Test
	public void shouldMapDoneScrumIssue() {	
		List<String> labels = Lists.newArrayList("");
		GitlabProject project = new GitlabProject();
		GitlabNamespace namespace = new GitlabNamespace();
		namespace.setId(23L);
		project.setNamespace(namespace);
		GitlabMilestone milestone = new GitlabMilestone();
		milestone.setId(23L);
		milestone.setDueDate("date");
		GitlabIssue issue = new GitlabIssue();
		issue.setState("closed");
		issue.setLabels(labels);
		issue.setProject(project);
		issue.setMilestone(milestone);
		List<String> inProgressLabelsForProject = Lists.newArrayList("Doing", "ToDo");
		ObjectId existingIssueId = new ObjectId();
		ObjectId gitlabCollectorId = new ObjectId();
		
		Feature result = mapper.mapToFeatureItem(issue , inProgressLabelsForProject , existingIssueId, gitlabCollectorId);
		
		assertNotNull(result);
		assertEquals("Done", result.getsStatus());
		assertEquals("23", result.getsSprintID());
	}
	
	@Test
	public void shouldMapUnknownKanbanIssue() {	
		List<String> labels = Lists.newArrayList("");
		GitlabProject project = new GitlabProject();
		GitlabNamespace namespace = new GitlabNamespace();
		namespace.setId(23L);
		project.setNamespace(namespace);
		GitlabMilestone milestone = new GitlabMilestone();
		milestone.setId(23L);
		GitlabIssue issue = new GitlabIssue();
		issue.setLabels(labels);
		issue.setProject(project);
		issue.setMilestone(milestone);
		List<String> inProgressLabelsForProject = Lists.newArrayList("Doing", "ToDo");
		ObjectId existingIssueId = new ObjectId();
		ObjectId gitlabCollectorId = new ObjectId();
		
		Feature result = mapper.mapToFeatureItem(issue , inProgressLabelsForProject , existingIssueId, gitlabCollectorId);
		
		assertNotNull(result);
		assertEquals("", result.getsStatus());
		assertEquals(FeatureCollectorConstants.SPRINT_KANBAN, result.getsSprintID());
	}

}
