package com.capitalone.dashboard.util;

import org.junit.Assert;
import org.junit.Test;

public class HygieiaUtilsTest {
    @Test
    public void checkForEmptyStringValuesTest() {
        boolean result = HygieiaUtils.checkForEmptyStringValues("", "test1", "test2");
        Assert.assertTrue(result);

        result = HygieiaUtils.checkForEmptyStringValues("test0", "test1", "test2");
        Assert.assertFalse(result);
    }
}
