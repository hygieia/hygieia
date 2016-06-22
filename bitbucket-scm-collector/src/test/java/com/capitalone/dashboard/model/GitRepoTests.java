package com.capitalone.dashboard.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class GitRepoTests {

	
	private GitRepo gitRepo1;
	private GitRepo gitRepo2;
	private GitRepo gitRepo3;
	
	
	
	@Before
    public void init() {
		gitRepo1 = new GitRepo();
		gitRepo1.setRepoUrl("https://bitbucket.com/capitalone/Hygiea.git");
		gitRepo1.setBranch("master");
		gitRepo2 = new GitRepo();
		gitRepo2.setRepoUrl("https://bitbucket.com/capitalone/Hygiea.git");
        gitRepo2.setBranch("master");
        gitRepo3=new GitRepo();
        gitRepo3.setRepoUrl("https://bitbucket.com/capitalone/Hygieas.git");
        gitRepo3.setBranch("master");
        }
	
	
	
	@Test
	public void testEquals() throws Exception {
		boolean x=gitRepo1.equals(gitRepo2);
		assertTrue(x);
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashcode1=gitRepo1.hashCode();
		int hashcode2=gitRepo2.hashCode();
		
		assertEquals(hashcode1, hashcode2);
	}
	
	@Test
	public void testEqualsNegative() throws Exception {
			boolean y=gitRepo3.equals(gitRepo1);
			assertTrue(!y);
		}
	
	@Test
	public void testHashCodeNegative() throws Exception {
		int hashcode1=gitRepo1.hashCode();
		int hashcode3=gitRepo3.hashCode();
		assertTrue(hashcode1!=hashcode3);
	}
	
	


}
