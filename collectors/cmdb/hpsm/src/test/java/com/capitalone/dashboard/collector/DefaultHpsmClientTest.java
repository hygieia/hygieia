package com.capitalone.dashboard.collector;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultHpsmClientTest {
    private static final String QUERY_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    private DefaultHpsmClient defaultHpsmClient;
    @Mock
    private HpsmSettings hpsmSettings;

    @Before
    public void init() {
        defaultHpsmClient = new DefaultHpsmClient(hpsmSettings);
    }

    @Test
    public void getPreviousDateValue_Test() {
        defaultHpsmClient.setLastExecuted(DateTime.parse("2018-08-27T12:15:00").getMillis());
        DateTime nowDate = DateTime.parse("2018-08-28T12:15:00");
        DateTimeFormatter formatter = DateTimeFormat.forPattern(QUERY_DATE_FORMAT);
        String result = defaultHpsmClient.getPreviousDateValue(nowDate,2, 2, 15, formatter);
        Assert.assertEquals(result, "08/27/2018 12:00:00");

        defaultHpsmClient.setLastExecuted(DateTime.parse("2018-08-20T12:15:00").getMillis());
        result = defaultHpsmClient.getPreviousDateValue(nowDate,2, 2, 15, formatter);
        Assert.assertEquals(result, nowDate.minusDays(2).toString(formatter));

        defaultHpsmClient.setLastExecuted(DateTime.parse("2018-08-27T12:15:00").getMillis());
        nowDate = DateTime.parse("2018-08-20T12:15:00");
        result = defaultHpsmClient.getPreviousDateValue(nowDate,2, 2, 15, formatter);
        Assert.assertEquals(result, nowDate.minusDays(2).toString(formatter));

        nowDate = new DateTime();
        defaultHpsmClient.setLastExecuted(DateTime.parse("2018-08-26T12:15:00").getMillis());
        result = defaultHpsmClient.getPreviousDateValue(nowDate,0, 2, 15, formatter);
        Assert.assertEquals(result, nowDate.minusDays(2).toString(formatter));
    }
}
