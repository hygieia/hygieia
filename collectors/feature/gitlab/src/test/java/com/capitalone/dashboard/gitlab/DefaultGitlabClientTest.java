package com.capitalone.dashboard.gitlab;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.collector.FeatureSettings;
import com.capitalone.dashboard.gitlab.model.GitlabBoard;
import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabList;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.model.Project;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGitlabClientTest {
	
	@Mock
	private RestOperations restOperations;
	
	@Mock
	private FeatureSettings settings;
	
	@Mock
	private GitlabUriUtility gitlabUrlUtility;
	
	@Mock
	private ResponseEntity<GitlabProject[]> projectResponse;
	
	@Mock
	private ResponseEntity<GitlabBoard[]> boardResponse;
	
	@Mock
	private ResponseEntity<GitlabIssue[]> issueResponse;
	
	@Mock
	private HttpHeaders headers;
	
	@InjectMocks
	private DefaultGitlabClient gitlabClient;

	private URI uri;

	@Before
	public void setup() throws URISyntaxException {
		uri = new URI("http://google.com");
		when(gitlabUrlUtility.buildAuthenticationHeader()).thenReturn(new HttpEntity<String>("body"));
	}

	@Test
	public void shouldGetInProgressLabelsForProject() {
		String projectId = "hygieia";
		String teamId = "capitalone";
		Project project = new Project(teamId, projectId);
		GitlabBoard board = new GitlabBoard();
		GitlabList gitlabList = new GitlabList();
		GitlabLabel label = new GitlabLabel();
		gitlabList.setLabel(label);
		board.setLists(Lists.newArrayList(gitlabList));
		GitlabBoard[] boards = {board};
		when(gitlabUrlUtility.buildBoardsUri(project)).thenReturn(uri);
		when(restOperations.exchange(eq(uri), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabBoard[].class))).thenReturn(boardResponse);
		when(boardResponse.getBody()).thenReturn(boards);
		
		List<GitlabLabel> result = gitlabClient.getInProgressLabelsForProject(project);
		
		assertNotNull(result);
		assertTrue(result.contains(label));
	}
	
	@Test
	public void shouldGetIssuesForProject() {	
	    String projectId = "hygieia";
        String teamId = "capitalone";
        Project project = new Project(teamId, projectId);
		GitlabProject gitlabProject = new GitlabProject();
		gitlabProject.setId(23L);
		GitlabIssue gitlabIssue = new GitlabIssue();
		GitlabIssue[] issues = {gitlabIssue};
		when(gitlabUrlUtility.buildIssuesForProjectUri(project)).thenReturn(uri);
		when(restOperations.exchange(eq(uri), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabIssue[].class))).thenReturn(issueResponse);
		when(issueResponse.getBody()).thenReturn(issues);
		
		List<GitlabIssue> result = gitlabClient.getIssuesForProject(project);
	
		assertNotNull(result);
		assertSame(gitlabIssue, result.get(0));
	}
	
	@Test
    public void shouldGetProjectsForTeam() {   
        Long projectId = 1L;
        GitlabProject project = new GitlabProject();
        project.setId(projectId);
        GitlabProject[] projects = {project};
        
        when(gitlabUrlUtility.buildProjectsForTeamUri("1")).thenReturn(uri);
        when(restOperations.exchange(eq(uri), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabProject[].class))).thenReturn(projectResponse);
        when(projectResponse.getBody()).thenReturn(projects);
        
        List<GitlabProject> result = gitlabClient.getProjectsForTeam("1");
    
        assertNotNull(result);
        assertSame(project, result.get(0));
    }
	
}
