package com.capitalone.dashboard.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Widget;


/**
 * Created by syq410 on 2/23/17.
 */
public class PipelineUtilsTest {

    private static final ObjectId DASHBOARD_ID = new ObjectId();

    @Test
    public void testOrderForStages() {
        Map<String, String> ordermap = PipelineUtils.getOrderForStages(setupDashboard());
        assertEquals(ordermap.get("0"), "Commit");
        assertEquals(ordermap.get("1"), "Build");
        assertEquals(ordermap.get("2"), "dev");
        assertEquals(ordermap.get("3"), "qa");
        assertEquals(ordermap.get("4"), "int");

    }

    private Dashboard setupDashboard() {
        Dashboard rt = new Dashboard("Capone", "hygieia", new Application("hygieia", new Component()), new Owner("owner", AuthType.STANDARD), DashboardType.Team);

        Widget pipelineWidget = new Widget();
        pipelineWidget.setName("pipeline");
        Map<String, String> mappings = new HashMap<>();
        mappings.put("dev", "DEV");
        mappings.put("qa", "QA");
        mappings.put("int", "INT");
        pipelineWidget.getOptions().put("mappings", mappings);

        Map<String, String> order = new HashMap<>();
        order.put("0", "dev");
        order.put("1", "qa");
        order.put("2", "int");
        pipelineWidget.getOptions().put("order", order);

        rt.getWidgets().add(pipelineWidget);

        rt.setId(DASHBOARD_ID);

        return rt;
    }

}
