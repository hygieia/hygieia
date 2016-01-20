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

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl;

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
	protected static String jiraApiContext;
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
	public static void setUpBeforeClass() throws Exception {
		jiraCredentials = "dW5pY286c3BhcmtsZXMK";
		jiraBaseUri = "http://fake.jira.com/";
		jiraApiContext = "rest/api/2";
		proxyUri = "http://proxy.com";
		proxyPort = "8080";

		logger.info("Beginning tests for com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -3);
		yesterday = dateFormat.format(cal.getTime());
		StringBuilder canonicalYesterday = new StringBuilder(yesterday);
		// canonicalYesterday.replace(10, 11, "%20");

		query = "updatedDate >= '" + canonicalYesterday + "' ORDER BY updated";
	}

	/**
	 * Runs actions after test is complete.
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		jiraCredentials = null;
		jiraBaseUri = null;
		jiraApiContext = null;
		proxyUri = null;
		proxyPort = null;
		yesterday = null;
		query = null;
	}

	/**
	 * Performs these actions before each test.
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Performs these actions after each test completes.
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		jiraDataFactory = null;
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#JiraDataFactoryImpl()}
	 * .
	 */
	@Test
	public void testJiraDataFactoryImpl_StandardInit() {
		jiraDataFactory = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUri, jiraApiContext);
		assertEquals("The compared contructed page size values did not match", 1000,
				jiraDataFactory.getPageSize());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#JiraDataFactoryImpl(int)}
	 * .
	 */
	@Test
	public void testJiraDataFactoryImpl_InitWithExplicitPageSize() {
		jiraDataFactory = new JiraDataFactoryImpl(500, jiraCredentials, jiraBaseUri, jiraApiContext);
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
		jiraDataFactory = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUri, jiraApiContext,
				proxyUri, proxyPort);
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
		jiraDataFactory = new JiraDataFactoryImpl(null, jiraBaseUri, jiraApiContext, proxyUri,
				proxyPort);
		assertEquals("The compared contructed page size values did not match", 1000,
				jiraDataFactory.getPageSize());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#buildBasicQuery(java.lang.String)}
	 * .
	 */
	@Test
	public void testBuildBasicQuery_ConstructorPageSize_SelectedPageSize() {
		jiraDataFactory = new JiraDataFactoryImpl(1, jiraCredentials, jiraBaseUri, jiraApiContext);
		jiraDataFactory.buildBasicQuery(query);
		assertNotNull("The basic query was created", jiraDataFactory.getBasicQuery());
		assertEquals("The page size was accurate", 1, jiraDataFactory.getPageSize());
		assertEquals("The page index was accurate", 0, jiraDataFactory.getPageIndex());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#buildPagingQuery(int)}
	 * .
	 */
	@Test
	public void testBuildPagingQuery_OverrideConstructorPageSize_OverriddenPageSize() {
		jiraDataFactory = new JiraDataFactoryImpl(1, jiraCredentials, jiraBaseUri, jiraApiContext);
		jiraDataFactory.buildBasicQuery(query);
		jiraDataFactory.buildPagingQuery(30);
		assertNotNull("The basic query was created", jiraDataFactory.getPagingQuery());
		assertEquals("The page size was accurate", 1, jiraDataFactory.getPageSize());
		assertEquals("The page index was accurate", 30, jiraDataFactory.getPageIndex());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#getPagingQueryResponse()}
	 * .
	 */
	@Test
	public void testGetPagingQueryResponse() {
		logger.debug("RUNNING TEST FOR PAGING QUERY RESPONSE");
		jiraDataFactory = new JiraDataFactoryImpl(1, jiraCredentials, jiraBaseUri, jiraApiContext,
				proxyUri, proxyPort);
		jiraDataFactory.buildBasicQuery(query);
		jiraDataFactory.buildPagingQuery(0);
		try {
			JSONArray rs = jiraDataFactory.getPagingQueryResponse();

			/*
			 * Testing actual JSON for values
			 */
			JSONArray dataMainArry = new JSONArray();
			JSONObject dataMainObj = new JSONObject();
			dataMainArry = (JSONArray) rs.get(0);
			dataMainObj = (JSONObject) dataMainArry.get(0);

			logger.info("Paging query response: " + dataMainObj.get("fields").toString());
			// fields
			assertTrue("No valid Number was found",
					dataMainObj.get("fields").toString().length() >= 1);
		} catch (NullPointerException npe) {
			fail("There was a problem with an object used to connect to Jira during the test:\n"
					+ npe.getMessage() + " caused by: " + npe.getCause());
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			fail("The object returned from Jira had no JSONObjects in it during the test; try increasing the scope of your test case query and try again.\n"
					+ aioobe.getMessage() + " caused by: " + aioobe.getCause());
		} catch (IndexOutOfBoundsException ioobe) {
			logger.info("JSON artifact may be empty - re-running test to prove this out...");

			JSONArray rs = jiraDataFactory.getPagingQueryResponse();

			/*
			 * Testing actual JSON for values
			 */
			String strRs = new String();
			strRs = rs.toString();

			logger.info("Paging query response: " + strRs);
			assertEquals(
					"There was nothing returned from Jira that is consistent with a valid response.",
					"[[]]", strRs);
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test:\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.jira.JiraDataFactoryImpl#getQueryResponse(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetQueryResponse() {
		logger.debug("RUNNING TEST FOR BASIC QUERY RESPONSE");
		jiraDataFactory = new JiraDataFactoryImpl(jiraCredentials, jiraBaseUri, jiraApiContext,
				proxyUri, proxyPort);
		jiraDataFactory.buildBasicQuery(query);
		try {
			JSONArray rs = jiraDataFactory.getQueryResponse();

			/*
			 * Testing actual JSON for values
			 */
			JSONArray dataMainArry = new JSONArray();
			JSONObject dataMainObj = new JSONObject();
			dataMainArry = (JSONArray) rs.get(0);
			dataMainObj = (JSONObject) dataMainArry.get(0);

			logger.info("Basic query response: " + dataMainObj.get("fields").toString());
			// fields
			assertTrue("No valid Number was found",
					dataMainObj.get("fields").toString().length() >= 1);
		} catch (NullPointerException npe) {
			fail("There was a problem with an object used to connect to Jira during the test\n"
					+ npe.getMessage() + " caused by: " + npe.getCause());
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			fail("The object returned from Jira had no JSONObjects in it during the test; try increasing the scope of your test case query and try again\n"
					+ aioobe.getMessage() + " caused by: " + aioobe.getCause());
		} catch (IndexOutOfBoundsException ioobe) {
			logger.info("JSON artifact may be empty - re-running test to prove this out...");
			JSONArray rs = jiraDataFactory.getQueryResponse();

			/*
			 * Testing actual JSON for values
			 */
			String strRs = new String();
			strRs = rs.toString();

			logger.info("Basic query response: " + strRs);
			assertEquals(
					"There was nothing returned from Jira that is consistent with a valid response.",
					"[[]]", strRs);
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to Jira during the test\n"
					+ e.getMessage() + " caused by: " + e.getCause());
		}
	}
}
