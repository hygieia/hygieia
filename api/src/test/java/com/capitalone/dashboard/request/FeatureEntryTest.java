package com.capitalone.dashboard.request;

import com.capitalone.dashboard.misc.HygieiaException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class FeatureEntryTest {
    @Test
    public void FeatureEntry_toWidgetOptions_Test() throws HygieiaException {
        DashboardRemoteRequest.Entry entry = new DashboardRemoteRequest.FeatureEntry();

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put("featureTool", "Jira");
        allOptions.put("projectName", "TestProject");
        allOptions.put("projectId", "123");
        allOptions.put("teamName", "TestTeam");
        allOptions.put("teamId", "321");
        allOptions.put("estimateMetricType", "storypoints");
        allOptions.put("sprintType", "kanban");
        allOptions.put("listType", "epics");
        allOptions.put("showStatus", "showStatus");

        entry.setOptions(allOptions);
        Map<String, Object> options = entry.toWidgetOptions();

        Assert.assertEquals(10, options.size());

        Assert.assertEquals("feature0", options.get("id"));
        Assert.assertEquals("Jira", options.get("featureTool"));
        Assert.assertEquals("TestProject", options.get("projectName"));
        Assert.assertEquals("123", options.get("projectId"));
        Assert.assertEquals("TestTeam", options.get("teamName"));
        Assert.assertEquals("321", options.get("teamId"));
        Assert.assertEquals("storypoints", options.get("estimateMetricType"));
        Assert.assertEquals("kanban", options.get("sprintType"));
        Assert.assertEquals("epics", options.get("listType"));
        Assert.assertEquals("showStatus", options.get("showStatus"));
    }
}
