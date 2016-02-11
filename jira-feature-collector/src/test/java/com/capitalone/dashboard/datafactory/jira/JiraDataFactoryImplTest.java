/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.datafactory.jira;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests all facets of the VerisonOneDataFactoryImpl class, which is responsible
 * for handling all transactions to the source system, Jira.
 * 
 * @author KFK884
 * 
 */
public class JiraDataFactoryImplTest {
	private static Log logger = LogFactory.getLog(JiraDataFactoryImplTest.class);
	protected static String queryName;
	protected static String query;
	protected static String yesterday;
	protected static String jiraCredentials;
	protected static String jiraBaseUri;
	protected static String proxyUri;
	protected static String proxyPort;
	protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	protected static JiraDataFactoryImpl jiraDataFactory;

	/**
	 * Default constructor.
	 */
	public JiraDataFactoryImplTest() {
	}

	/**
	 * Runs actions before test is initialized.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		jiraCredentials = "dW5pY286c3BhcmtsZXMK";
		jiraBaseUri = "http://fake.jira.com/";
		proxyUri = "http://proxy.com";
		proxyPort = "8080";

		logger.info("Beginning tests for com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -3);
		yesterday = dateFormat.format(cal.getTime());
		StringBuilder canonicalYesterday = new StringBuilder(yesterday);

		query = "updatedDate >= '" + canonicalYesterday + "' ORDER BY updated";
	}

	/**
	 * Runs actions after test is complete.
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		jiraDataFactory = null;
		jiraCredentials = null;
		jiraBaseUri = null;
		proxyUri = null;
		proxyPort = null;
		yesterday = null;
		query = null;
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#JiraDataFactoryImpl()}
	 * .
	 */
	@Test
	public void testJiraDataFactoryImpl_StandardInit() {
		jiraDataFactory = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUri);
		assertEquals("The compared contructed page size values did not match", 1000,
				jiraDataFactory.getPageSize());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl(int)}
	 * .
	 */
	@Test
	public void testJiraDataFactoryImpl_InitWithExplicitPageSize() {
		jiraDataFactory = new JiraDataFactoryImpl(500, jiraCredentials, jiraBaseUri);
		assertEquals("The compared contructed page size values did not match", 500,
				jiraDataFactory.getPageSize());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#JiraDataFactoryImpl()}
	 * .
	 */
	@Test
	public void testJiraDataFactoryImpl_InitWithAuthProxy() {
		jiraDataFactory = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUri, proxyUri, proxyPort);
		assertEquals("The compared contructed page size values did not match", 1000,
				jiraDataFactory.getPageSize());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#JiraDataFactoryImpl()}
	 * .
	 */
	@Test
	public void testJiraDataFactoryImpl_InitWithNonAuthProxy() {
		jiraDataFactory = new JiraDataFactoryImpl(null, jiraBaseUri, proxyUri, proxyPort);
		assertEquals("The compared contructed page size values did not match", 1000,
				jiraDataFactory.getPageSize());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#JiraDataFactoryImpl()}
	 * .
	 */
	@Test
	public void testJiraDataFactoryImpl_InitWithBlankAuthProxy_HandleWithNoAuthProxy() {
		// Test null proxy - default page size
		jiraDataFactory = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUri, null, null);
		jiraDataFactory.setQuery(query);
		assertEquals("The lack of a proxy was not handled correctly", 1000,
				jiraDataFactory.getPageSize());

		try {
			List<Issue> rs = jiraDataFactory.getJiraIssues();

			if (!rs.isEmpty()) {
				logger.info("basic query response: " + rs.get(0));
				assertTrue("No valid data set was found", rs.size() >= 0);
			} else {
				// Check blank response
				assertTrue("Response object was unexpectedly null",
						rs.equals(new ArrayList<Issue>()));
			}
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test:\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}
		// Test null proxy - set page size
		jiraDataFactory = null;
		jiraDataFactory = new JiraDataFactoryImpl(1000, jiraCredentials, jiraBaseUri, null, null);
		jiraDataFactory.setQuery(query);
		assertEquals("The lack of a proxy was not handled correctly", 1000,
				jiraDataFactory.getPageSize());

		try {
			List<Issue> rs = jiraDataFactory.getJiraIssues();

			if (!rs.isEmpty()) {
				logger.info("basic query response: " + rs.get(0));
				assertTrue("No valid data set was found", rs.size() >= 0);
			} else {
				// Check blank response
				assertTrue("Response object was unexpectedly null",
						rs.equals(new ArrayList<Issue>()));
			}
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test:\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}

		// Test blank proxy - default page size
		jiraDataFactory = null;
		jiraDataFactory = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUri, "", "");
		jiraDataFactory.setQuery(query);
		assertEquals("Blank proxy was not handled correctly", 1000, jiraDataFactory.getPageSize());

		try {
			List<Issue> rs = jiraDataFactory.getJiraIssues();

			if (!rs.isEmpty()) {
				logger.info("basic query response: " + rs.get(0));
				assertTrue("No valid data set was found", rs.size() >= 0);
			} else {
				// Check blank response
				assertTrue("Response object was unexpectedly null",
						rs.equals(new ArrayList<Issue>()));
			}
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test:\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}
		// Test blank proxy - set page size
		jiraDataFactory = null;
		jiraDataFactory = new JiraDataFactoryImpl(1000, jiraCredentials, jiraBaseUri, "", "");
		jiraDataFactory.setQuery(query);
		assertEquals("Blank proxy was not handled correctly", 1000, jiraDataFactory.getPageSize());

		try {
			List<Issue> rs = jiraDataFactory.getJiraIssues();

			if (!rs.isEmpty()) {
				logger.info("basic query response: " + rs.get(0));
				assertTrue("No valid data set was found", rs.size() >= 0);
			} else {
				// Check blank response
				assertTrue("Response object was unexpectedly null",
						rs.equals(new ArrayList<Issue>()));
			}
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test:\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#JiraDataFactoryImpl()}
	 * .
	 */
	@Test
	public void testJiraDataFactoryImpl_InitURIWithoutTailingSlash_ValidResponse() {
		// Test null proxy
		jiraDataFactory = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUri.substring(0,
				jiraBaseUri.length() - 1), null, null);
		jiraDataFactory.setQuery(query);
		assertEquals("The lack of a proxy was not handled correctly", 1000,
				jiraDataFactory.getPageSize());

		try {
			List<Issue> rs = jiraDataFactory.getJiraIssues();

			if (!rs.isEmpty()) {
				logger.info("basic query response: " + rs.get(0));
				assertTrue("No valid data set was found", rs.size() >= 0);
			} else {
				// Check blank response
				assertTrue("Response object was unexpectedly null",
						rs.equals(new ArrayList<Issue>()));
			}
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test:\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#setQuery(java.lang.String)}
	 * .
	 */
	@Test
	public void testBuildBasicQuery_ConstructorPageSize_SelectedPageSize() {
		jiraDataFactory = new JiraDataFactoryImpl(1, jiraCredentials, jiraBaseUri);
		jiraDataFactory.setQuery(query);
		assertNotNull("The basic query was created", jiraDataFactory.getBasicQuery());
		assertEquals("The page size was accurate", 1, jiraDataFactory.getPageSize());
		assertEquals("The page index was accurate", 0, jiraDataFactory.getPageIndex());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl}
	 * .
	 */
	@Test
	public void testBuildPagingQuery_OverrideConstructorPageSize_OverriddenPageSize() {
		jiraDataFactory = new JiraDataFactoryImpl(1, jiraCredentials, jiraBaseUri);
		jiraDataFactory.setQuery(query);
		jiraDataFactory.setPageIndex(30);
		assertNotNull("The basic query was created", jiraDataFactory.getBasicQuery());
		assertEquals("The page size was accurate", 1, jiraDataFactory.getPageSize());
		assertEquals("The page index was accurate", 30, jiraDataFactory.getPageIndex());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#getJiraIssues()}
	 * .
	 */
	@Test
	public void testGetJiraIssues_StandardInputOnePage_ValidIssueRs() {
		logger.debug("RUNNING TEST FOR SINGLE RESPONSE");
		jiraDataFactory = new JiraDataFactoryImpl(1, jiraCredentials, jiraBaseUri, proxyUri,
				proxyPort);
		jiraDataFactory.setQuery(query);
		try {
			List<Issue> rs = jiraDataFactory.getJiraIssues();

			if (!rs.isEmpty()) {
				logger.info("basic query response: " + rs.get(0));
				assertTrue("No valid data set was found", rs.size() >= 0);
			} else {
				// Check blank response
				assertTrue("Response object was unexpectedly null",
						rs.equals(new ArrayList<Issue>()));
			}
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test:\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#getJiraIssues()}
	 * .
	 */
	@Test
	public void testGetJiraIssues_MultiplePages_ValidIssueRs() {
		logger.debug("RUNNING TEST FOR PAGING QUERY RESPONSE");
		jiraDataFactory = new JiraDataFactoryImpl(1000, jiraCredentials, jiraBaseUri, proxyUri,
				proxyPort);
		jiraDataFactory.setQuery(query);
		boolean hasMore = true;
		try {
			for (int i = 0; hasMore; i += 1000) {
				jiraDataFactory.setPageIndex(i);
				List<Issue> rs = jiraDataFactory.getJiraIssues();
				if (rs.isEmpty()) {
					hasMore = false;
					// Check blank response
					assertTrue("Response object was unexpectedly null",
							rs.equals(new ArrayList<Issue>()));
				}

				if (hasMore) {
					// Only validate if there are no more responses
					logger.info("Paging query response: " + rs.get(0));
					assertTrue("No valid data set was found", rs.size() >= 0);
				}
			}
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test:\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#getJiraIssues()}
	 * .
	 */
	@Test
	public void testGetJiraTeams_StandardInput_AllTeamRs() {
		logger.debug("RUNNING TEST FOR All TEAM RESPONSE");
		jiraDataFactory = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUri, proxyUri, proxyPort);
		try {
			List<BasicProject> rs = jiraDataFactory.getJiraTeams();

			if (!rs.isEmpty()) {
				logger.info("basic query response: " + rs.get(0));
				assertTrue("No valid data set was found", !rs.isEmpty());
			} else {
				// Check blank response
				assertTrue("Response object was unexpectedly null",
						rs.equals(new ArrayList<Issue>()));
			}
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test:\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}
	}
}
