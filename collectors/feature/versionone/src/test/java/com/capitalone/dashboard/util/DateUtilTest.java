package com.capitalone.dashboard.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtilTest {
	private static Logger logger = LoggerFactory.getLogger("DateUtilTest");
	protected static DateUtil classUnderTest;
	private static int MAX_KANBAN_ITERATION_LENTH = 28;

	@Before
	public void setUp() throws Exception {
		logger.info("Beginning tests for com.capitalone.dashboard.collector.DateUtilTest");
		classUnderTest = new DateUtil();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for sprint length via string parse
	 */
	@Test
	public void testEvaluateSprintLength_ValidDateSpread_ValidRs() {
		String startScrum = "2016-05-01T00:00:00.000000";
		String endScrum = "2016-05-14T00:00:00.000000";
		String startKanban = "2016-04-01T00:00:00.000000";
		String endKanban = "2016-08-01T00:00:00.000000";

		assertTrue("The response was not indicative of a scrum iteration",
				classUnderTest.evaluateSprintLength(startScrum, endScrum, DateUtilTest.MAX_KANBAN_ITERATION_LENTH));
		assertFalse("The response was not indicative of a kanban iteration",
				classUnderTest.evaluateSprintLength(startKanban, endKanban, DateUtilTest.MAX_KANBAN_ITERATION_LENTH));
	}

	/**
	 * Negative tests for sprint length via string parse
	 */
	@Test
	public void testEvaluateSprintLength_InvalidDateSpread_KanbanRs() {
		String startScrum = null;
		String endScrum = null;
		String startKanban = "2016-4-1T00:00:00.000000";
		String endKanban = "2016-4-14T00:00:00.000000";

		assertFalse("The response was not indicative of a kanban iteration",
				classUnderTest.evaluateSprintLength(startScrum, endScrum, DateUtilTest.MAX_KANBAN_ITERATION_LENTH));
		assertFalse("The response was not indicative of a kanban iteration",
				classUnderTest.evaluateSprintLength(startKanban, endKanban, DateUtilTest.MAX_KANBAN_ITERATION_LENTH));
	}
}
