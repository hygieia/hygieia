package com.capitalone.dashboard.datafactory.versionone.test;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;

/**
 * Tests all facets of the VerisonOneDataFactoryImpl class, which is responsible
 * for handling all transactions to the source system, VersionOne.
 *
 * @author KFK884
 *
 */
public class VersionOneDataFactoryImplTest {
	private static Logger logger = LoggerFactory
			.getLogger("VersionOneDataFactoryImplTest");
	protected static String queryName;
	protected static String query;
	protected static String yesterday;
	protected static DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	protected static Map<String, String> auth;
	protected static VersionOneDataFactoryImpl v1DataFactory;

	/**
	 * Default constructor.
	 */
	public VersionOneDataFactoryImplTest() {
	}

	/**
	 * Runs actions before test is initialized.
	 *
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logger.info("Beginning tests for com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl");
		auth = new HashMap<String, String>();
		// TODO:  Include your own company proxy
		auth.put("v1ProxyUrl", "");
		// TODO:  Include your own base uri for VersionOne
		auth.put("v1BaseUri", "");
		// TODO:  Include your own v1 auth token
		auth.put("v1AccessToken", "");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -3);
		yesterday = dateFormat.format(cal.getTime());
		yesterday = yesterday.replace(" ", "T");

		query = "from: Story\n" + "select:\n" + "  - Number\n" + "filter:\n"
				+ "  - ChangeDate>'" + yesterday + "'\n"
				+ "  - (IsDeleted='False'|IsDeleted='True')\n";
		v1DataFactory = new VersionOneDataFactoryImpl(auth);
	}

	/**
	 * Runs actions after test is complete.
	 *
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		v1DataFactory = null;
		auth = null;
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
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl#buildPagingQuery(int)}
	 * .
	 */
	@Test
	public void testBuildPagingQuery() {
		v1DataFactory.setPageSize(1);
		v1DataFactory.buildPagingQuery(30);
		assertNotNull("The basic query was created",
				v1DataFactory.getPagingQuery());
		assertEquals("The page size was accurate", 1,
				v1DataFactory.getPageSize());
		assertEquals("The page index was accurate", 30,
				v1DataFactory.getPageIndex());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl#getPagingQueryResponse()}
	 * .
	 */
	@Ignore
	@Test
	public void testGetPagingQueryResponse() {
		v1DataFactory.setPageSize(1);
		v1DataFactory.buildBasicQuery(query);
		v1DataFactory.buildPagingQuery(0);
		try {
			JSONArray rs = v1DataFactory.getPagingQueryResponse();

			/*
			 * Testing actual JSON for values
			 */
			JSONArray dataMainArry = new JSONArray();
			JSONObject dataMainObj = new JSONObject();
			dataMainArry = (JSONArray) rs.get(0);
			dataMainObj = (JSONObject) dataMainArry.get(0);

			// number
			assertTrue("No valid Number was found", dataMainObj.get("Number")
					.toString().length() >= 7);
		} catch (NullPointerException npe) {
			fail("There was a problem with an object used to connect to VersionOne during the test");
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			fail("The object returned from VersionOne had no JSONObjects in it during the test; try increasing the scope of your test case query and try again.");
		} catch (IndexOutOfBoundsException ioobe) {
			logger.info("JSON artifact may be empty - re-running test to prove this out...");

			JSONArray rs = v1DataFactory.getPagingQueryResponse();

			/*
			 * Testing actual JSON for values
			 */
			String strRs = new String();
			strRs = rs.toString();

			assertEquals(
					"There was nothing returned from VersionOne that is consistent with a valid response.",
					"[[]]", strRs);
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to VersionOne during the test");
		}
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl#VersionOneDataFactoryImpl()}
	 * .
	 */
	@Test
	public void testVersionOneDataFactoryImpl() {
		assertEquals("The compared contructed page size values did not match",
				2000, v1DataFactory.getPageSize());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl#VersionOneDataFactoryImpl(int)}
	 * .
	 */
	@Test
	public void testVersionOneDataFactoryImplInt() {
		v1DataFactory.setPageSize(1000);
		assertEquals("The compared contructed page size values did not match",
				1000, v1DataFactory.getPageSize());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl#buildBasicQuery(java.lang.String)}
	 * .
	 */
	@Test
	public void testBuildBasicQuery() {
		v1DataFactory.setPageSize(1);
		v1DataFactory.buildBasicQuery(query);
		assertNotNull("The basic query was created",
				v1DataFactory.getBasicQuery());
		assertEquals("The page size was accurate", 1,
				v1DataFactory.getPageSize());
		assertEquals("The page index was accurate", 0,
				v1DataFactory.getPageIndex());
	}

	/**
	 * Test method for
	 * {@link com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl#getQueryResponse(java.lang.String)}
	 * .
	 */
	@Ignore
	@Test
	public void testGetQueryResponse() {
		v1DataFactory.setPageSize(1);
		v1DataFactory.buildBasicQuery(query);
		try {
			JSONArray rs = v1DataFactory.getQueryResponse();

			/*
			 * Testing actual JSON for values
			 */
			JSONArray dataMainArry = new JSONArray();
			JSONObject dataMainObj = new JSONObject();
			dataMainArry = (JSONArray) rs.get(0);
			dataMainObj = (JSONObject) dataMainArry.get(0);

			// number
			assertTrue("No valid Number was found", dataMainObj.get("Number")
					.toString().length() >= 7);
		} catch (NullPointerException npe) {
			fail("There was a problem with an object used to connect to VersionOne during the test");
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			fail("The object returned from VersionOne had no JSONObjects in it during the test; try increasing the scope of your test case query and try again.");
		} catch (IndexOutOfBoundsException ioobe) {
			logger.info("JSON artifact may be empty - re-running test to prove this out...");

			JSONArray rs = v1DataFactory.getQueryResponse();

			/*
			 * Testing actual JSON for values
			 */
			String strRs = new String();
			strRs = rs.toString();

			assertEquals(
					"There was nothing returned from VersionOne that is consistent with a valid response.",
					"[[]]", strRs);
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to VersionOne during the test");
		}
	}

}
