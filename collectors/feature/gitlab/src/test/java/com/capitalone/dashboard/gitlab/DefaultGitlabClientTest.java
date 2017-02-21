package com.capitalone.dashboard.gitlab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.collector.FeatureSettings;
import com.capitalone.dashboard.gitlab.model.GitlabBoard;
import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabList;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
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
	private ResponseEntity<GitlabTeam[]> teamResponse;
	
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
	public void shouldGetOnePageOfTeamsNoHeader() {
		GitlabTeam team = new GitlabTeam();
		GitlabTeam[] teams = {team};
		when(gitlabUrlUtility.buildTeamsUri()).thenReturn(uri);
		when(restOperations.exchange(eq(uri), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabTeam[].class))).thenReturn(teamResponse);
		when(teamResponse.getBody()).thenReturn(teams);
		
		List<GitlabTeam> result = gitlabClient.getTeams();
		
		assertNotNull(result);
		assertTrue(result.contains(team));
	}
	
	@Test
	public void shouldGetOnePageOfProjects() {
		GitlabProject project = new GitlabProject();
		GitlabProject[] projects = {project};
		String teamId = "teamId";
		ScopeOwnerCollectorItem team = new ScopeOwnerCollectorItem();
		team.setTeamId(teamId);
		when(gitlabUrlUtility.buildProjectsUri()).thenReturn(uri);
		when(restOperations.exchange(eq(uri), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabProject[].class))).thenReturn(projectResponse);
		when(projectResponse.getHeaders()).thenReturn(headers);
		when(headers.get("X-Next-Page")).thenReturn(Lists.newArrayList(""));
		when(projectResponse.getBody()).thenReturn(projects);
		
		List<GitlabProject> result = gitlabClient.getProjects();
		
		assertNotNull(result);
		assertTrue(result.contains(project));
	}
	
	@Test
	public void shouldGetMultiplePagesOfProjects() {
		GitlabProject project = new GitlabProject();
		GitlabProject[] projects = {project};
		String teamId = "teamId";
		ScopeOwnerCollectorItem team = new ScopeOwnerCollectorItem();
		team.setTeamId(teamId);
		when(gitlabUrlUtility.buildProjectsUri()).thenReturn(uri);
		when(gitlabUrlUtility.updatePage(isA(URI.class), anyString())).thenReturn(uri);
		when(restOperations.exchange(eq(uri), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabProject[].class))).thenReturn(projectResponse);
		when(projectResponse.getHeaders()).thenReturn(headers);
		when(headers.get("X-Next-Page")).thenReturn(Lists.newArrayList("2")).thenReturn(Lists.newArrayList("2")).thenReturn(Lists.newArrayList(""));
		when(projectResponse.getBody()).thenReturn(projects);
		
		List<GitlabProject> result = gitlabClient.getProjects();
		
		assertNotNull(result);
		assertTrue(result.contains(project));
		assertEquals(2, result.size());
	}
	
	@Test
	public void shouldGetInProgressLabelsForProject() {
		Long projectId = 23L;
		GitlabBoard board = new GitlabBoard();
		GitlabList gitlabList = new GitlabList();
		GitlabLabel label = new GitlabLabel();
		gitlabList.setLabel(label);
		board.setLists(Lists.newArrayList(gitlabList));
		GitlabBoard[] boards = {board};
		when(gitlabUrlUtility.buildBoardsUri("23")).thenReturn(uri);
		when(restOperations.exchange(eq(uri), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabBoard[].class))).thenReturn(boardResponse);
		when(boardResponse.getBody()).thenReturn(boards);
		
		List<GitlabLabel> result = gitlabClient.getInProgressLabelsForProject(projectId);
		
		assertNotNull(result);
		assertTrue(result.contains(label));
	}
	
	@Test
	public void shouldGetIssuesForProject() {	
		Long projectId = 1L;
		GitlabProject project = new GitlabProject();
		project.setId(projectId);
		GitlabIssue gitlabIssue = new GitlabIssue();
		GitlabIssue[] issues = {gitlabIssue};
		when(gitlabUrlUtility.buildIssuesForProjectUri("1")).thenReturn(uri);
		when(restOperations.exchange(eq(uri), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabIssue[].class))).thenReturn(issueResponse);
		when(issueResponse.getBody()).thenReturn(issues);
		
		List<GitlabIssue> result = gitlabClient.getIssuesForProject(project);
	
		assertNotNull(result);
		assertSame(gitlabIssue, result.get(0));
		assertSame(project, result.get(0).getProject());
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
	
	@Test
	public void shouldGetProjectById() {
	    Long projectId = 1L;
        GitlabProject project = new GitlabProject();
        project.setId(projectId);
        ResponseEntity<GitlabProject> response = new ResponseEntity<GitlabProject>(project, HttpStatus.OK);
        
        when(gitlabUrlUtility.buildProjectsByIdUri("1")).thenReturn(uri);
        when(restOperations.exchange(eq(uri), eq(HttpMethod.GET), isA(HttpEntity.class), eq(GitlabProject.class))).thenReturn(response);
        
        GitlabProject result = gitlabClient.getProjectById("1");
    
        assertNotNull(result);
        assertSame(project, result);
	}

}
