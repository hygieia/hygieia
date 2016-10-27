package com.capitalone.dashboard.collecteur;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.capitalone.dashboard.model.Commit;

public class GitlabCommitsResponseMapperTest {
	
	private GitlabCommitsResponseMapper gitlabResponseMapper;
	
	@Before
	public void setup() {
		gitlabResponseMapper = new GitlabCommitsResponseMapper();
	}

	@Test
	public void shouldMapOneCommit() {
		String jsonString = "[{\"id\":\"fakeId\",\"short_id\":\"fake\",\"title\":\"corrected docker run command for versionOne\",\"author_name\":\"fake author\",\"author_email\":\"fake.author@fake.com\",\"created_at\":\"2016-10-25T07:33:47.000-07:00\",\"message\":\"message\"}]";
		String repoUrl = "http://domain.com";
		String repoBranch = "master";
		long timestamp = new DateTime("2016-10-25T07:33:47.000-07:00").getMillis();
		List<Commit> commits = gitlabResponseMapper.map(jsonString, repoUrl, repoBranch);
		Commit commit = commits.get(0);
		
		assertEquals(repoUrl, commit.getScmUrl());
		assertEquals(repoBranch, commit.getScmBranch());
		assertEquals("fakeId", commit.getScmRevisionNumber());
		assertEquals("fake author", commit.getScmAuthor());
		assertEquals("message", commit.getScmCommitLog());
		assertEquals(timestamp, commit.getScmCommitTimestamp());
		assertEquals(1, commit.getNumberOfChanges());
	}
	
	@Test
	public void shouldHaveNullValuesIfNothingForKey() {
		String jsonString = "[{\"short_id\":\"fake\",\"title\":\"corrected docker run command for versionOne\",\"author_name\":\"fake author\",\"author_email\":\"fake.author@fake.com\",\"created_at\":\"2016-10-25T07:33:47.000-07:00\",\"message\":\"message\"}]";
		List<Commit> commits = gitlabResponseMapper.map(jsonString, null, null);
		Commit commit = commits.get(0);
		
		assertNull(commit.getScmRevisionNumber());
	}
	
	@Test
	public void shouldReturnEmptyJsonArrayWhenBadJsonString() {
		String jsonString = "[\"short_id\":\"fake\",\"title\":\"corrected docker run command for versionOne\",\"author_name\":\"fake author\",\"author_email\":\"fake.author@fake.com\",\"created_at\":\"2016-10-25T07:33:47.000-07:00\",\"message\":\"message\"}]";
		List<Commit> commits = gitlabResponseMapper.map(jsonString, null, null);
		
		assertEquals(0, commits.size());
	}

}
