package com.capitalone.dashboard.utils;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class UtilitiesTest {
    @Test
    public void parseDateWithoutFractionEmpty() {
        String date = Utilities.parseDateWithoutFraction("");
        assertEquals( "", date);
    }
    @Test
    public void parseDateWithoutFraction() {
        String date = Utilities.parseDateWithoutFraction("2018-05-16T11:21:26.000-0400");
        assertEquals("2018-05-16T11:21:26", date);
    }
    @Test
    public void parseDateWithoutFractionNull() {
        String date = Utilities.parseDateWithoutFraction(null);
        assertEquals("", date);
    }
}
