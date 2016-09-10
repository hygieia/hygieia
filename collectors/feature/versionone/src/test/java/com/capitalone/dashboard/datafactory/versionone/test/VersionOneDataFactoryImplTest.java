package com.capitalone.dashboard.datafactory.versionone.test;

import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.misc.HygieiaException;
import org.apache.commons.lang3.StringUtils;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests all facets of the VerisonOneDataFactoryImpl class, which is responsible
 * for handling all transactions to the source system, VersionOne.
 * 
 * @author KFK884
 * 
 */
public class VersionOneDataFactoryImplTest {
	private static Logger logger = LoggerFactory.getLogger("VersionOneDataFactoryImplTest");
	protected static String queryName;
	protected static String query;
	protected static String yesterday;
	protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		logger.info(
				"Beginning tests for com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl");
		auth = new HashMap<>();
		// TODO: Include your own company proxy
		auth.put("v1ProxyUrl", "");
		// TODO: Include your own base uri for VersionOne
		auth.put("v1BaseUri", "");
		// TODO: Include your own v1 auth token
		auth.put("v1AccessToken", "");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -3);
		yesterday = dateFormat.format(cal.getTime());
		yesterday = yesterday.replace(" ", "T");

		query = "from: Story\n" + "select:\n" + "  - Number\n" + "filter:\n" + "  - ChangeDate>'"
				+ yesterday + "'\n" + "  - (IsDeleted='False'|IsDeleted='True')\n";
		if (StringUtils.isNotEmpty(auth.get("v1BaseUri"))) {
			v1DataFactory = new VersionOneDataFactoryImpl(auth);
		} else {
			logger.warn(
					"Switching to generic V1 data factory connnection (with no auth). This should eventually be resolved by mocking a V1 response model in testing");
			v1DataFactory = new VersionOneDataFactoryImpl();
		}
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
        v1DataFactory.setBasicQuery(query);
		v1DataFactory.buildPagingQuery(30);
		assertNotNull("The basic query was created", v1DataFactory.getPagingQuery());
		assertEquals("The page size was accurate", 1, v1DataFactory.getPageSize());
		assertEquals("The page index was accurate", 30, v1DataFactory.getPageIndex());
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
        v1DataFactory.setBasicQuery(query);
		v1DataFactory.buildPagingQuery(0);
		try {
			JSONArray rs = v1DataFactory.getPagingQueryResponse();

			/*
			 * Testing actual JSON for values
			 */

            JSONArray dataMainArry = (JSONArray) rs.get(0);
            JSONObject dataMainObj = (JSONObject) dataMainArry.get(0);

			// number
			assertTrue("No valid Number was found",
					dataMainObj.get("Number").toString().length() >= 7);
		} catch (NullPointerException npe) {
			fail("There was a problem with an object used to connect to VersionOne during the test");
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			fail("The object returned from VersionOne had no JSONObjects in it during the test; try increasing the scope of your test case query and try again.");
		} catch (IndexOutOfBoundsException ioobe) {
			logger.info("JSON artifact may be empty - re-running test to prove this out...");

			JSONArray rs = new JSONArray();
			try {
				rs = v1DataFactory.getPagingQueryResponse();
			} catch (HygieiaException e) {
				fail("There was an unexpected problem while connecting to VersionOne during the test");
			}

			/*
			 * Testing actual JSON for values
			 */
			String strRs = rs.toString();

			assertEquals(
					"There was nothing returned from VersionOne that is consistent with a valid response.",
					"[[]]", strRs);
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to VersionOne during the test");
		}
	}


	@Test
	public void testVersionOneDataFactoryImpl() {
		assertEquals("The compared contructed page size values did not match", 2000,
				v1DataFactory.getPageSize());
	}


	@Test
	public void testVersionOneDataFactoryImplInt() {
		v1DataFactory.setPageSize(1000);
		assertEquals("The compared contructed page size values did not match", 1000,
				v1DataFactory.getPageSize());
	}


	@Test
	public void testBuildBasicQuery() {
		v1DataFactory.setPageSize(1);
        v1DataFactory.setBasicQuery(query);
		assertNotNull("The basic query was created", v1DataFactory.getBasicQuery());
		assertEquals("The page size was accurate", 1, v1DataFactory.getPageSize());
		assertEquals("The page index was accurate", 0, v1DataFactory.getPageIndex());
	}


	@Ignore
	@Test
	public void testGetQueryResponse() {
		v1DataFactory.setPageSize(1);
        v1DataFactory.setBasicQuery(query);
		try {
			JSONArray rs = v1DataFactory.getQueryResponse();

			/*
			 * Testing actual JSON for values
			 */
            JSONArray dataMainArry = (JSONArray) rs.get(0);
            JSONObject dataMainObj = (JSONObject) dataMainArry.get(0);

			// number
			assertTrue("No valid Number was found",
					dataMainObj.get("Number").toString().length() >= 7);
		} catch (NullPointerException npe) {
			fail("There was a problem with an object used to connect to VersionOne during the test");
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			fail("The object returned from VersionOne had no JSONObjects in it during the test; try increasing the scope of your test case query and try again.");
		} catch (IndexOutOfBoundsException ioobe) {
			logger.info("JSON artifact may be empty - re-running test to prove this out...");

			JSONArray rs = new JSONArray();
			try {
				rs = v1DataFactory.getQueryResponse();
			} catch (HygieiaException e) {
				fail("There was an unexpected problem while connecting to VersionOne during the test");
			}

			/*
			 * Testing actual JSON for values
			 */
			String strRs = rs.toString();

			assertEquals(
					"There was nothing returned from VersionOne that is consistent with a valid response.",
					"[[]]", strRs);
		} catch (Exception e) {
			fail("There was an unexpected problem while connecting to VersionOne during the test");
		}
	}

}
