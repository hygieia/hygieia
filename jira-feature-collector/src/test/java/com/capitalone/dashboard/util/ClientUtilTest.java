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

package com.capitalone.dashboard.util;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.client.Sprint;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests key facets of the ClientUtilTest class, which is responsible for
 * orchestrating updates to the local repositories based on data from the source
 * system.
 * 
 * @author KFK884
 * 
 */
public class ClientUtilTest {
	private static Logger logger = LoggerFactory.getLogger("ClientUtilTest");
	protected static ClientUtil classUnderTest;

	/**
	 * Default constructor
	 */
	public ClientUtilTest() {
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logger.info("Beginning tests for com.capitalone.dashboard.collector.ClientUtilTest");
		classUnderTest = ClientUtil.getInstance();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests capabilities of string sanitizing method to, in fact, sanitize data
	 */
	@Test
	public void testSanitizeResponse() {
		String badEncoding;
		byte[] b = { (byte) 0xc3, (byte) 0x28 };
		badEncoding = new String(b);

		assertEquals("Santized test object did not match expected output", "Happy Path",
				classUnderTest.sanitizeResponse("Happy Path"));
		assertEquals("Santized test object did not match expected output", "",
				classUnderTest.sanitizeResponse(""));
		assertEquals("Santized test object did not match expected output", "",
				classUnderTest.sanitizeResponse("NULL"));
		assertEquals("Santized test object did not match expected output", "",
				classUnderTest.sanitizeResponse("Null"));
		assertEquals("Santized test object did not match expected output", "",
				classUnderTest.sanitizeResponse("null"));
		assertEquals("Santized test object did not match expected output", "",
				classUnderTest.sanitizeResponse(null));
		// This test is slightly misleading - there is no good way natively to
		// handle for removal of character set mapping tests in Java
		assertNotEquals("Santized test object did not match expected output",
				"[INVALID NON UTF-8 ENCODING]", classUnderTest.sanitizeResponse(badEncoding));
	}

	/**
	 * Tests capabilities of converting source system date format to standard
	 * localized format
	 */
	@Test
	public void testToCanonicalDate() {
		String testLongDateFormat = new String("2015-01-03T00:00:00.0000000");
		String testLongDateFormatJira = new String("2015-06-15T12:49:08.005-0400");
		String testBlank = "";

		assertEquals("Actual date format did not match expected date format output",
				dateLocal("2015-01-03T00:00:00.000") + "0000", classUnderTest.toCanonicalDate(testLongDateFormat));
		assertEquals("Actual date format did not match expected date format output",
				dateLocal("2015-06-15T12:49:08.005-04:00") + "0000",
				classUnderTest.toCanonicalDate(testLongDateFormatJira));
		assertEquals("Actual date format did not match expected date format output", "",
				classUnderTest.toCanonicalDate(testBlank));
		assertEquals("Actual date format did not match expected date format output", "",
				classUnderTest.toCanonicalDate(null));
	}

	/**
	 * Tests capabilities of creating a list artifact
	 */
	@Test
	public void testToCanonicalList() {
		List<String> testStr = null;

		assertNotNull("Canonical list was null", classUnderTest.toCanonicalList(testStr));

		testStr = new ArrayList<String>();
		testStr.add("This is a test value");
		assertNotNull("Canonical list was null", classUnderTest.toCanonicalList(testStr));
	}

	/**
	 * Tests capabilities of converting source system Sprint link format to
	 * standard JSONArray sprint format
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testToCanonicalSprintJSON() {
		String testSprint = new String(
				"com.atlassian.greenhopper.service.sprint.Sprint@2e1b47d5[id=561,rapidViewId=244,state=ACTIVE,name=Sharknado- Sprint 12,startDate=2015-06-04T09:22:11.525-04:00,endDate=2015-06-16T19:00:00.000-04:00,completeDate=<null>,sequence=561]");
		JSONObject expectedBlank = new JSONObject();
		JSONObject expectedFull = new JSONObject();
		expectedFull.put("id", "561");
		expectedFull.put("rapidViewId", "244");
		expectedFull.put("state", "ACTIVE");
		expectedFull.put("name", "Sharknado- Sprint 12");
		expectedFull.put("startDate", "2015-06-04T09:22:11.525-04:00");
		expectedFull.put("endDate", "2015-06-16T19:00:00.000-04:00");
		expectedFull.put("completeDate", "<null>");
		expectedFull.put("sequence", "561");

		assertEquals("Actual sprint format did not match expected sprint format", expectedBlank,
				classUnderTest.toCanonicalSprintJSON(null));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.size(), classUnderTest.toCanonicalSprintJSON(testSprint).size());
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("id"), classUnderTest.toCanonicalSprintJSON(testSprint).get("id"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("rapidViewId"), classUnderTest.toCanonicalSprintJSON(testSprint)
						.get("rapidViewId"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("state"),
				classUnderTest.toCanonicalSprintJSON(testSprint).get("state"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("name"),
				classUnderTest.toCanonicalSprintJSON(testSprint).get("name"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("startDate"), classUnderTest.toCanonicalSprintJSON(testSprint)
						.get("startDate"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("endDate"),
				classUnderTest.toCanonicalSprintJSON(testSprint).get("endDate"));
		assertEquals("Actual sprint format did not match expected sprint format", "",
				classUnderTest.toCanonicalSprintJSON(testSprint).get("completeDate"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("sequence"),
				classUnderTest.toCanonicalSprintJSON(testSprint).get("sequence"));

		testSprint = new String(
				"com.atlassian.greenhopper.service.sprint.Sprint@78d510cf[id=590,rapidViewId=265,state=FUTURE,name=Chassis Sprint 1.1,startDate=2015-06-04T09:22:11.525-04:00,endDate=<null>,completeDate=<null>,sequence=590]");
		testSprint = classUnderTest.sanitizeResponse(testSprint);

		expectedBlank = new JSONObject();
		expectedFull = new JSONObject();
		expectedFull.put("id", "590");
		expectedFull.put("rapidViewId", "265");
		expectedFull.put("state", "FUTURE");
		expectedFull.put("name", "Chassis Sprint 1.1");
		expectedFull.put("startDate", "2015-06-04T09:22:11.525-04:00");
		expectedFull.put("endDate", "<null>");
		expectedFull.put("completeDate", "<null>");
		expectedFull.put("sequence", "590");

		assertEquals("Actual sprint format did not match expected sprint format", expectedBlank,
				classUnderTest.toCanonicalSprintJSON(null));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.size(), classUnderTest.toCanonicalSprintJSON(testSprint).size());
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("id"), classUnderTest.toCanonicalSprintJSON(testSprint).get("id"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("rapidViewId"), classUnderTest.toCanonicalSprintJSON(testSprint)
						.get("rapidViewId"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("state"),
				classUnderTest.toCanonicalSprintJSON(testSprint).get("state"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("name"),
				classUnderTest.toCanonicalSprintJSON(testSprint).get("name"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("startDate"), classUnderTest.toCanonicalSprintJSON(testSprint)
						.get("startDate"));
		assertEquals("Actual sprint format did not match expected sprint format", "",
				classUnderTest.toCanonicalSprintJSON(testSprint).get("endDate"));
		assertEquals("Actual sprint format did not match expected sprint format", "",
				classUnderTest.toCanonicalSprintJSON(testSprint).get("completeDate"));
		assertEquals("Actual sprint format did not match expected sprint format",
				expectedFull.get("sequence"),
				classUnderTest.toCanonicalSprintJSON(testSprint).get("sequence"));
	}

	/**
	 * Tests ability to convert local MongoDB ISO format into date time format
	 * used by Jira during queries
	 */
	@Test
	public void testToNativeDate() {
		String testCanonicalDate = "2015-03-01T00:00:00.000000";

		assertEquals(
				"Transformed date does not meet expected output: "
						+ classUnderTest.toNativeDate(testCanonicalDate), "2015-03-01 00:00",
				classUnderTest.toNativeDate(testCanonicalDate));

		testCanonicalDate = "2015-03-01";
		assertEquals(
				"Transformed date does not meet expected output: "
						+ classUnderTest.toNativeDate(testCanonicalDate), "2015-03-01 00:00",
				classUnderTest.toNativeDate(testCanonicalDate));

		testCanonicalDate = null;
		assertEquals(
				"Transformed date does not meet expected output: "
						+ classUnderTest.toNativeDate(testCanonicalDate), "1900-01-01 00:00",
				classUnderTest.toNativeDate(testCanonicalDate));
	}

	@Test
	public void testToHours_NullStringLiteral_ZeroStringOutput() {
		assertEquals("The response was unexpectedly not zero", "0", classUnderTest.toHours("0"));
		assertEquals("The response was unexpectedly not zero", "0", classUnderTest.toHours(null));
		assertEquals("The response was unexpectedly not zero", "0", classUnderTest.toHours("null"));
	}
	
	@Test
	public void testParseSprint() throws ParseException {
		String sprintRaw1 = "com.atlassian.greenhopper.service.sprint.Sprint@2189d27[id=2144,rapidViewId=1645,state=OPEN,name=Sprint 18,startDate=2016-05-31T14:06:46.350-04:00,endDate=2016-06-16T17:06:00.000-04:00,completeDate=2016-06-20T14:21:57.131-04:00,sequence=2144]";
		String sprintRaw2 = "com.atlassian.greenhopper.service.sprint.Sprint@2189d27[id=2145,rapidViewId=1645,state=OPEN,name=Sprint 18, with comma,startDate=2016-05-31T14:06:46.350-04:00,endDate=2016-06-16T17:06:00.000-04:00,completeDate=2016-06-20T14:21:57.131-04:00,sequence=2144]";

		Sprint sprint1 = classUnderTest.parseSprint(sprintRaw1);
		assertEquals(Long.valueOf(2144), sprint1.getId());
		assertEquals("OPEN", sprint1.getState());
		assertEquals("Sprint 18", sprint1.getName());
		assertEquals("2016-05-31T14:06:46.350-04:00", sprint1.getStartDateStr());
		assertEquals("2016-06-16T17:06:00.000-04:00", sprint1.getEndDateStr());
		assertEquals("2016-06-20T14:21:57.131-04:00", sprint1.getCompleteDateStr());
		assertEquals(2144, sprint1.getSequence());
		
		Sprint sprint2 = classUnderTest.parseSprint(sprintRaw2);
		assertEquals(Long.valueOf(2145), sprint2.getId());
		assertEquals("Sprint 18, with comma", sprint2.getName());
	}
	
	private String dateLocal(String date) {
		DateTime dt = ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(date);
		return ISODateTimeFormat.dateHourMinuteSecondMillis().print(dt);
	}
}
