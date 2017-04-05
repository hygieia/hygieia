package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.capitalone.dashboard.gitlab.GitlabCommitsResponseMapper;
import com.capitalone.dashboard.gitlab.model.GitlabCommit;
import com.capitalone.dashboard.model.Commit;

public class GitlabCommitsResponseMapperTest {
	
	private GitlabCommitsResponseMapper gitlabResponseMapper;
	
	@Before
	public void setup() {
		gitlabResponseMapper = new GitlabCommitsResponseMapper();
	}

	@Test
	public void shouldMapOneCommit() {
	    String createdAt = "2016-10-25T07:33:47.000-07:00";
	    
		GitlabCommit gitlabCommit = new GitlabCommit();
		gitlabCommit.setId("fakeId");
		gitlabCommit.setAuthorName("fake author");
		gitlabCommit.setMessage("message");
        gitlabCommit.setCreatedAt(createdAt);
		GitlabCommit[] gitlabCommits = {gitlabCommit};
		
		String repoUrl = "http://domain.com";
		String repoBranch = "master";
		long timestamp = new DateTime(createdAt).getMillis();
		List<Commit> commits = gitlabResponseMapper.map(gitlabCommits, repoUrl, repoBranch);
		Commit commit = commits.get(0);
		
		assertEquals(repoUrl, commit.getScmUrl());
		assertEquals(repoBranch, commit.getScmBranch());
		assertEquals("fakeId", commit.getScmRevisionNumber());
		assertEquals("fake author", commit.getScmAuthor());
		assertEquals("message", commit.getScmCommitLog());
		assertEquals(timestamp, commit.getScmCommitTimestamp());
		assertEquals(1, commit.getNumberOfChanges());
	}

}
