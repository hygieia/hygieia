package com.capitalone.dashboard.request;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import com.capitalone.dashboard.model.FeatureCollector;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class FeatureEntryTest {
    @Test
    public void FeatureEntry_toCollectorItem_Test() throws HygieiaException {
        FeatureCollector collector = prototype();

        DashboardRemoteRequest.Entry entry = new DashboardRemoteRequest.FeatureEntry();

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(FeatureCollectorConstants.TOOL_TYPE, "Jira");
        allOptions.put(FeatureCollectorConstants.PROJECT_NAME, "TestProject");
        allOptions.put(FeatureCollectorConstants.PROJECT_ID, "123");
        allOptions.put(FeatureCollectorConstants.TEAM_NAME, "TestTeam");
        allOptions.put(FeatureCollectorConstants.TEAM_ID, "321");
        allOptions.put(FeatureCollectorConstants.ESTIMATE_METRIC_TYPE, "storypoints");
        allOptions.put(FeatureCollectorConstants.SPRINT_TYPE, "kanban");
        allOptions.put(FeatureCollectorConstants.LIST_TYPE, "epics");
        allOptions.put(FeatureCollectorConstants.SHOW_STATUS, "showStatus");

        entry.setOptions(allOptions);

        CollectorItem collectorItem = entry.toCollectorItem(collector);

        Map<String, Object> options = collectorItem.getOptions();
        Assert.assertEquals(5, options.size());
        Assert.assertEquals("Jira", options.get(FeatureCollectorConstants.TOOL_TYPE));
        Assert.assertEquals("TestProject", options.get(FeatureCollectorConstants.PROJECT_NAME));
        Assert.assertEquals("123", options.get(FeatureCollectorConstants.PROJECT_ID));
        Assert.assertEquals("TestTeam", options.get(FeatureCollectorConstants.TEAM_NAME));
        Assert.assertEquals("321", options.get(FeatureCollectorConstants.TEAM_ID));
    }

    @Test(expected = HygieiaException.class)
    public void FeatureEntry_toCollectorItem_Exception_Test() throws HygieiaException {
        FeatureCollector collector = prototype();

        DashboardRemoteRequest.Entry entry = new DashboardRemoteRequest.FeatureEntry();

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(FeatureCollectorConstants.TOOL_TYPE, "Jira");
        allOptions.put(FeatureCollectorConstants.PROJECT_NAME, "TestProject");
        allOptions.put(FeatureCollectorConstants.PROJECT_ID, "123");
        allOptions.put(FeatureCollectorConstants.TEAM_NAME, "TestTeam");
        allOptions.put(FeatureCollectorConstants.TEAM_ID, "321");
        allOptions.put("SomeNonAllowedOption", "SomeNonAllowedOption");
        entry.setOptions(allOptions);

        entry.toCollectorItem(collector);
    }

    private FeatureCollector prototype() {
        FeatureCollector protoType = new FeatureCollector();
        protoType.setName(FeatureCollectorConstants.JIRA);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.setCollectorType(CollectorType.AgileTool);
        protoType.setLastExecuted(System.currentTimeMillis());

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(FeatureCollectorConstants.TOOL_TYPE, "");
        allOptions.put(FeatureCollectorConstants.PROJECT_NAME, "");
        allOptions.put(FeatureCollectorConstants.PROJECT_ID, "");
        allOptions.put(FeatureCollectorConstants.TEAM_NAME, "");
        allOptions.put(FeatureCollectorConstants.TEAM_ID, "");
        allOptions.put(FeatureCollectorConstants.ESTIMATE_METRIC_TYPE, "");
        allOptions.put(FeatureCollectorConstants.SPRINT_TYPE, "");
        allOptions.put(FeatureCollectorConstants.LIST_TYPE, "");
        allOptions.put(FeatureCollectorConstants.SHOW_STATUS, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(FeatureCollectorConstants.TOOL_TYPE, "");
        uniqueOptions.put(FeatureCollectorConstants.PROJECT_NAME, "");
        uniqueOptions.put(FeatureCollectorConstants.PROJECT_ID, "");
        uniqueOptions.put(FeatureCollectorConstants.TEAM_NAME, "");
        uniqueOptions.put(FeatureCollectorConstants.TEAM_ID, "");

        protoType.setUniqueFields(uniqueOptions);

        return protoType;
    }
}
