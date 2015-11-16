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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.util.ClientUtil;

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
		classUnderTest = new ClientUtil();
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

		assertEquals("Santized test string did not match expected output",
				"Happy Path", classUnderTest.sanitizeResponse("Happy Path"));
		assertEquals("Santized test string did not match expected output", "",
				classUnderTest.sanitizeResponse(""));
		assertEquals("Santized test string did not match expected output", "",
				classUnderTest.sanitizeResponse("NULL"));
		assertEquals("Santized test string did not match expected output", "",
				classUnderTest.sanitizeResponse("Null"));
		assertEquals("Santized test string did not match expected output", "",
				classUnderTest.sanitizeResponse("null"));
		assertEquals("Santized test string did not match expected output", "",
				classUnderTest.sanitizeResponse(null));
		// This test is slightly misleading - there is no good way natively to
		// handle for removal of character set mapping tests in Java
		assertNotEquals("Santized test string did not match expected output",
				"[INVALID NON UTF-8 ENCODING]",
				classUnderTest.sanitizeResponse(badEncoding));
	}

	/**
	 * Tests capabilities of converting VersionOne date format to standard
	 * localized format
	 */
	@Test
	public void testToCanonicalDate() {
		String testLongDateFormat = new String("2015-01-03T00:00:00.0000000");
		String testBlank = "";

		assertEquals(
				"Actual date format did not match expected date format output",
				"2015-01-03T00:00:00.0000000",
				classUnderTest.toCanonicalDate(testLongDateFormat));
		assertEquals(
				"Actual date format did not match expected date format output",
				"", classUnderTest.toCanonicalDate(testBlank));
	}
}
