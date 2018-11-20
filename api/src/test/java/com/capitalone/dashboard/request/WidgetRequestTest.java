package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class WidgetRequestTest {
    @Test
    public void widgetTest() {
        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put("id", "feature0");
        allOptions.put(FeatureCollectorConstants.TOOL_TYPE, "Jira");
        allOptions.put(FeatureCollectorConstants.PROJECT_NAME, "TestProject");
        allOptions.put(FeatureCollectorConstants.PROJECT_ID, "123");
        allOptions.put(FeatureCollectorConstants.TEAM_NAME, "TestTeam");
        allOptions.put(FeatureCollectorConstants.TEAM_ID, "321");

        WidgetRequest widgetRequest = new WidgetRequest();
        widgetRequest.setName("feature");
        widgetRequest.setOptions(allOptions);
        Widget widget = widgetRequest.widget();

        Map<String, Object> options = widget.getOptions();
        Assert.assertEquals(6, options.size());
        Assert.assertEquals("feature0", options.get("id"));
        Assert.assertEquals("Jira", options.get(FeatureCollectorConstants.TOOL_TYPE));
        Assert.assertEquals("TestProject", options.get(FeatureCollectorConstants.PROJECT_NAME));
        Assert.assertEquals("123", options.get(FeatureCollectorConstants.PROJECT_ID));
        Assert.assertEquals("TestTeam", options.get(FeatureCollectorConstants.TEAM_NAME));
        Assert.assertEquals("321", options.get(FeatureCollectorConstants.TEAM_ID));
    }
}
