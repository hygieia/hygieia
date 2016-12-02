package com.capitalone.dashboard.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class GitlabGitRepoTest {
	
	private GitlabGitRepo repo1;
	private GitlabGitRepo repo2;
	private GitlabGitRepo repo3;
	
	@Before
	public void setup() {
		repo1 = new GitlabGitRepo();
		repo1.setRepoUrl("url");
		repo2 = new GitlabGitRepo();
		repo2.setRepoUrl("url");
		repo3 = new GitlabGitRepo();
		repo3.setRepoUrl("different");
	}

	@Test
	public void testEquals() {
		assertFalse(repo1.equals(null));
		assertTrue(repo1.equals(repo1));
		assertFalse(repo1.equals(new Object()));
		assertTrue(repo1.equals(repo2));
		assertFalse(repo1.equals(repo3));
	}
	
	@Test
	public void testHashCode() {
		assertEquals(repo1.hashCode(), repo2.hashCode());
		assertNotEquals(repo1.hashCode(), repo3.hashCode());
	}

}
