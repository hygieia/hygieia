package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.Widget;
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
        allOptions.put("featureTool", "Jira");
        allOptions.put("projectName", "TestProject");
        allOptions.put("projectId", "123");
        allOptions.put("teamName", "TestTeam");
        allOptions.put("teamId", "321");

        WidgetRequest widgetRequest = new WidgetRequest();
        widgetRequest.setName("AgileTool");
        widgetRequest.setOptions(allOptions);
        Widget widget = widgetRequest.widget();

        Map<String, Object> options = widget.getOptions();
        Assert.assertEquals(6, options.size());
        Assert.assertEquals("feature0", options.get("id"));
        Assert.assertEquals("Jira", options.get("featureTool"));
        Assert.assertEquals("TestProject", options.get("projectName"));
        Assert.assertEquals("123", options.get("projectId"));
        Assert.assertEquals("TestTeam", options.get("teamName"));
        Assert.assertEquals("321", options.get("teamId"));
    }

    @Test
    public void widgetTest2() {
        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put("id", "feature0");
        allOptions.put("featureTool", "Jira");
        allOptions.put("projectName", "TestProject");
        allOptions.put("projectId", "123");
        allOptions.put("teamName", "TestTeam");
        allOptions.put("teamId", "321");
		
		WidgetRequest widgetRequest = new WidgetRequest();
        widgetRequest.setName("feature");
        widgetRequest.setOptions(allOptions);
        Widget widget = widgetRequest.widget();

        Map<String, Object> options = widget.getOptions();
        Assert.assertEquals(6, options.size());
        Assert.assertEquals("feature0", options.get("id"));
        Assert.assertEquals("Jira", options.get("featureTool"));
        Assert.assertEquals("TestProject", options.get("projectName"));
        Assert.assertEquals("123", options.get("projectId"));
        Assert.assertEquals("TestTeam", options.get("teamName"));
        Assert.assertEquals("321", options.get("teamId"));
    }
}
