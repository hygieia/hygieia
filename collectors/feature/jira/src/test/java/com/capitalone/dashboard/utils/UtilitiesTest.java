package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.utils.Utilities;
import com.capitalone.dashboard.model.Sprint;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UtilitiesTest {
    @Test
    public void parseDateWithoutFractionEmpty() {
        String date = Utilities.parseDateWithoutFraction("");
        assertEquals( "", date);
        //2018-05-16T11:21:26.000-0400
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
