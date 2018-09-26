package com.capitalone.dashboard.collector;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBaseClientTest {
    @Mock
    private HpsmSettings hpsmSettings;

    @InjectMocks
    private DefaultBaseClient defaultBaseClient;

    @Test
    public void environmentCheck_Test() {
        List<String> environmentList = new ArrayList<>();
        environmentList.add("Production");
        environmentList.add("Pre-Production");
        environmentList.add("Non-Production");

        when(hpsmSettings.getIncidentEnvironments()).thenReturn(environmentList);
        boolean result = defaultBaseClient.environmentCheck("Production");
        Assert.assertTrue(result);

        result = defaultBaseClient.environmentCheck(null);
        Assert.assertTrue(result);

        result = defaultBaseClient.environmentCheck("");
        Assert.assertTrue(result);

        result = defaultBaseClient.environmentCheck("Test");
        Assert.assertFalse(result);
    }

    @Test
    public void getAffectedItem_Test() {
        String affectedItem = "BAPSOMETHING";
        String service = "ASVSOMETHING";

        String result = defaultBaseClient.getAffectedItem(affectedItem, service);
        Assert.assertEquals(affectedItem, result);

        affectedItem = "ENVSOMETHING";
        service = "CISOMETHING";

        result = defaultBaseClient.getAffectedItem(affectedItem, service);
        Assert.assertEquals(service, result);

        affectedItem = "ENVSOMETHING";
        service = "ASVSOMETHING";

        result = defaultBaseClient.getAffectedItem(affectedItem, service);
        Assert.assertEquals(affectedItem, result);

        affectedItem = "";
        service = "ASVSOMETHING";

        result = defaultBaseClient.getAffectedItem(affectedItem, service);
        Assert.assertEquals(service, result);
    }
}
