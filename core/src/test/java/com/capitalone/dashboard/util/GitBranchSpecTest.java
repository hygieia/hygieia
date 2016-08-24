package com.capitalone.dashboard.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class GitBranchSpecTest {
    @Test
    public void testMatch() {

        GitBranchSpec l = new GitBranchSpec("master");
        assertTrue(l.matches("origin/master"));
        assertFalse(l.matches("origin/something/master"));
        assertTrue(l.matches("master"));
        assertFalse(l.matches("dev"));
        
        
        GitBranchSpec est = new GitBranchSpec("origin/*/dev");
        
        assertFalse(est.matches("origintestdev"));
        assertTrue(est.matches("origin/test/dev"));
        assertFalse(est.matches("origin/test/release"));
        assertFalse(est.matches("origin/test/somthing/release"));
        
        GitBranchSpec s = new GitBranchSpec("origin/*");
        
        assertTrue(s.matches("origin/master"));
      
        GitBranchSpec m = new GitBranchSpec("**/magnayn/*");
        
        assertTrue(m.matches("origin/magnayn/b1"));
        assertTrue(m.matches("remote/origin/magnayn/b1"));
        assertTrue(m.matches("remotes/origin/magnayn/b1"));
      
        GitBranchSpec n = new GitBranchSpec("*/my.branch/*");
        
        assertTrue(n.matches("origin/my.branch/b1"));
        assertFalse(n.matches("origin/my-branch/b1"));
        assertFalse(n.matches("remote/origin/my.branch/b1"));
        assertTrue(n.matches("remotes/origin/my.branch/b1"));
      
        GitBranchSpec o = new GitBranchSpec("**");
        
        assertTrue(o.matches("origin/my.branch/b1"));
        assertTrue(o.matches("origin/my-branch/b1"));
        assertTrue(o.matches("remote/origin/my.branch/b1"));
        assertTrue(o.matches("remotes/origin/my.branch/b1"));
      
        GitBranchSpec p = new GitBranchSpec("*");

        assertTrue(p.matches("origin/x"));
        assertFalse(p.matches("origin/my-branch/b1"));
    }
    


    @Test
    public void testEmptyName() {
    	GitBranchSpec gitBranchSpec = new GitBranchSpec("");
    	assertEquals("**", gitBranchSpec.getName());
    }
    
    @Test
    public void testNullName() {
    	boolean correctExceptionThrown = false;
    	try {
    		GitBranchSpec gitBranchSpec = new GitBranchSpec(null);
    	} catch (IllegalArgumentException e) {
    		correctExceptionThrown = true;
    	}
    	assertTrue(correctExceptionThrown);
    }

    
    @Test
    public void testUsesRefsHeads() {
    	GitBranchSpec m = new GitBranchSpec("refs/heads/j*n*");
    	assertTrue(m.matches("refs/heads/jenkins"));
    	assertTrue(m.matches("refs/heads/jane"));
    	assertTrue(m.matches("refs/heads/jones"));

    	assertFalse(m.matches("origin/jenkins"));
    	assertFalse(m.matches("remote/origin/jane"));
    }
    
    @Test
    public void testUsesJavaPatternDirectlyIfPrefixedWithColon() {
    	GitBranchSpec m = new GitBranchSpec(":^(?!(origin/prefix)).*");
    	assertTrue(m.matches("origin"));
    	assertTrue(m.matches("origin/master"));
    	assertTrue(m.matches("origin/feature"));

    	assertFalse(m.matches("origin/prefix_123"));
    	assertFalse(m.matches("origin/prefix"));
    	assertFalse(m.matches("origin/prefix-abc"));
    }

    @Test
    public void testUsesJavaPatternWithRepetition() {
    	// match pattern from JENKINS-26842
    	GitBranchSpec m = new GitBranchSpec(":origin/release-\\d{8}");
    	assertTrue(m.matches("origin/release-20150101"));
    	assertFalse(m.matches("origin/release-2015010"));
    	assertFalse(m.matches("origin/release-201501011"));
    	assertFalse(m.matches("origin/release-20150101-something"));
    }

    @Test
    public void testUsesJavaPatternToExcludeMultipleBranches() {
        GitBranchSpec m = new GitBranchSpec(":^(?!origin/master$|origin/develop$).*");
        assertTrue(m.matches("origin/branch1"));
        assertTrue(m.matches("origin/branch-2"));
        assertTrue(m.matches("origin/master123"));
        assertTrue(m.matches("origin/develop-123"));
        assertFalse(m.matches("origin/master"));
        assertFalse(m.matches("origin/develop"));
    }
}
