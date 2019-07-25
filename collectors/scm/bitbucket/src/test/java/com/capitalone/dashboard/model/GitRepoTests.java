package com.capitalone.dashboard.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GitRepoTests {

	private GitRepoTestAdapter gitRepo1;
	private GitRepoTestAdapter gitRepo2;
	private GitRepoTestAdapter gitRepo3;

	@Before
    public void init() {
		gitRepo1 = buildInstance();
		gitRepo2 = buildInstance();
		gitRepo3 = buildInstance();
		gitRepo3.setRepoUrl("https://bitbucket.org/something-different");
	}

	@Test
	public void testEquals() {
		assertEquals(gitRepo1, gitRepo2);
	}

	@Test
	public void testHashCode() {
		int hashcode1 = gitRepo1.hashCode();
		int hashcode2 = gitRepo2.hashCode();

		assertEquals(hashcode1, hashcode2);
	}

	@Test
	public void testEqualsNegative() {
		assertNotEquals(gitRepo3, gitRepo1);
	}

	@Test
	public void testHashCodeNegative() {
		int hashcode1 = gitRepo1.hashCode();
		int hashcode3 = gitRepo3.hashCode();
		assertTrue(hashcode1 != hashcode3);
	}

	@Test
	public void getLastUpdateTimeDoesNotThrowCastExceptionWhenFieldIsDate() {
		// See #3024
        Date currentDate = new Date(System.currentTimeMillis() - 2000);
        gitRepo1.setLastUpdateTimeBypass(currentDate);

		assertTrue(DateTime.now().isAfter(gitRepo1.getLastUpdateTime()));
	}

	private GitRepoTestAdapter buildInstance() {
		GitRepoTestAdapter gitRepo = new GitRepoTestAdapter();
		gitRepo.setRepoUrl("https://bitbucket.com/capitalone/Hygiea.git");
		gitRepo.setBranch("master");
		return gitRepo;
	}
}
