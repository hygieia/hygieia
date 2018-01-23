package com.capitalone.dashboard.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GitHubRepoTests {

	
	private GitHubRepo githubRepo1;
	private GitHubRepo githubRepo2;
	private GitHubRepo githubRepo3;
	
	
	
	@Before
    public void init() {
		githubRepo1 = new GitHubRepo();
		githubRepo1.setRepoUrl("https://github.com/capitalone/Hygiea.git");
		githubRepo1.setBranch("master");
		githubRepo2 = new GitHubRepo();
		githubRepo2.setRepoUrl("https://github.com/capitalone/Hygiea.git");
        githubRepo2.setBranch("master");
        githubRepo3=new GitHubRepo();
        githubRepo3.setRepoUrl("https://github.com/capitalone/Hygieas.git");
        githubRepo3.setBranch("master");
        }
	
	
	
	@Test
	public void testEquals() {
		boolean x= Objects.equals(githubRepo1, githubRepo2);
		assertTrue(x);
	}
	
	@Test
	public void testHashCode() {
		int hashcode1=githubRepo1.hashCode();
		int hashcode2=githubRepo2.hashCode();
		
		assertEquals(hashcode1, hashcode2);
	}
	
	@Test
	public void testEqualsNegative() {
			boolean y= Objects.equals(githubRepo3, githubRepo1);
			assertTrue(!y);
		}
	
	@Test
	public void testHashCodeNegative() {
		int hashcode1=githubRepo1.hashCode();
		int hashcode3=githubRepo3.hashCode();
		assertTrue(hashcode1!=hashcode3);
	}
}
