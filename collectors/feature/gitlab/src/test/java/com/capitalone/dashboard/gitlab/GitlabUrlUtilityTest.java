package com.capitalone.dashboard.gitlab;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

public class GitlabUrlUtilityTest {
	
	private static final String host = "company.com";
	
	private GitlabUrlUtility urlUtility = new GitlabUrlUtility();

	@Test
	public void shouldBuildProjectsUri() {
		URI result = urlUtility.buildProjectsUri(host, "23"); 
		assertEquals("https://company.com/api/v3/groups/23/projects?per_page=100", result.toString());
	}
	
	@Test
	public void shouldBuildTeamUri() {
		URI result = urlUtility.buildTeamsUri(host); 
		assertEquals("https://company.com/api/v3/groups?per_page=100", result.toString());
	}
	
	@Test
	public void shouldBuildBoardsUri() {
		URI result = urlUtility.buildBoardsUri(host, "23"); 
		assertEquals("https://company.com/api/v3/projects/23/boards?per_page=100", result.toString());
	}
	
	@Test
	public void shouldBuildIssuesForProjectUri() {
		URI result = urlUtility.buildIssuesForProjectUri(host, "23"); 
		assertEquals("https://company.com/api/v3/projects/23/issues?per_page=100", result.toString());
	}
	
	@Test
	public void shouldUpdatePage() {
		URI uri = urlUtility.buildIssuesForProjectUri(host, "23"); 
		URI result = urlUtility.updatePage(uri, "2");
		assertEquals("https://company.com/api/v3/projects/23/issues?per_page=100&page=2", result.toString());
	}
	
}
