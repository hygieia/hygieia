package com.capitalone.dashboard.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GitHubRepoTests {

	
	private GitHubRepo githubRepo1;
	private GitHubRepo githubRepo2;
	private GitHubRepo githubRepo3;
	private GitHubRepo githubRepo4;
	
	
	
	@Before
    public void init() {
		githubRepo1 = new GitHubRepo();
		githubRepo1.setRepoUrl("https://github.com/capitalone/Hygiea.git");
		githubRepo1.setDefaultBranch("master");
		githubRepo1.setBranches(new ArrayList<String>());
		githubRepo2 = new GitHubRepo();
		githubRepo2.setRepoUrl("https://github.com/capitalone/Hygiea.git");
        githubRepo2.setDefaultBranch("master");
		githubRepo2.setBranches(new ArrayList<String>());
		githubRepo3=new GitHubRepo();
		githubRepo3.setRepoUrl("https://github.com/capitalone/Hygieas.git");
		githubRepo3.setDefaultBranch("master");
		githubRepo3.setBranches(new ArrayList<String>());
		githubRepo4=new GitHubRepo();
		githubRepo4.setRepoUrl("https://github.com/capitalone/Hygiea.git");
		githubRepo4.setDefaultBranch("master");
		List<String> branches = new ArrayList<String>();
		branches.add("master");
		githubRepo4.setBranches(branches);
        }
	
	
	
	@Test
	public void testEquals() throws Exception {
		boolean x=githubRepo1.equals(githubRepo2);
		assertTrue(x);
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashcode1=githubRepo1.hashCode();
		int hashcode2=githubRepo2.hashCode();
		
		assertEquals(hashcode1, hashcode2);
	}
	
	@Test
	public void testEqualsNegative() throws Exception {
			boolean y=githubRepo3.equals(githubRepo1);
			assertTrue(!y);
			boolean z=githubRepo2.equals(githubRepo4);
			assertTrue(!z);
		}
	
	@Test
	public void testHashCodeNegative() throws Exception {
		int hashcode1=githubRepo1.hashCode();
		int hashcode3=githubRepo3.hashCode();
		assertTrue(hashcode1!=hashcode3);
	}
}
