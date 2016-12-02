package com.capitalone.dashboard.client;

import com.capitalone.dashboard.collector.BaseClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BaseClientTest {
    private static Logger logger = LoggerFactory.getLogger(BaseClientTest.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("Beginning tests for com.capitalone.dashboard.collector.BaseClientTest");
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
        byte[] b = {(byte) 0xc3, (byte) 0x28};
        badEncoding = new String(b);

        assertEquals("Santized test string did not match expected output",
                "Happy Path", BaseClient.sanitizeResponse("Happy Path"));
        assertEquals("Santized test string did not match expected output", "",
                BaseClient.sanitizeResponse(""));
        assertEquals("Santized test string did not match expected output", "",
                BaseClient.sanitizeResponse("NULL"));
        assertEquals("Santized test string did not match expected output", "",
                BaseClient.sanitizeResponse("Null"));
        assertEquals("Santized test string did not match expected output", "",
                BaseClient.sanitizeResponse("null"));
        assertEquals("Santized test string did not match expected output", "",
                BaseClient.sanitizeResponse(null));
        // This test is slightly misleading - there is no good way natively to
        // handle for removal of character set mapping tests in Java
        assertNotEquals("Santized test string did not match expected output",
                "[INVALID NON UTF-8 ENCODING]",
                BaseClient.sanitizeResponse(badEncoding));
    }
}