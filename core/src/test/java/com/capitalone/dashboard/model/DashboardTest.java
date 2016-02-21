package com.capitalone.dashboard.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DashboardTest {

    @Test
    public void findEnvironmentMappings(){
        Dashboard dashboard = makeTeamDashboard("template", "title", "appName", "comp1", "comp2");
        dashboard.getWidgets().add(makePipelineWidget("DEV", "QA", null, null, "PROD"));
        Map<PipelineStageType, String> expected = new HashMap<>();
        expected.put(PipelineStageType.Dev, "DEV");
        expected.put(PipelineStageType.QA, "QA");
        expected.put(PipelineStageType.Prod, "PROD");

        Map<PipelineStageType, String> actual = dashboard.findEnvironmentMappings();
        assertEquals(expected, actual);
    }

    @Test
    public void findEnvironmentMappings_no_mappings_configured(){
        Dashboard dashboard = makeTeamDashboard("template", "title", "appName", "comp1", "comp2");
        Map<PipelineStageType, String> expected = new HashMap<>();
        Map<PipelineStageType, String> actual = dashboard.findEnvironmentMappings();
        assertEquals(expected, actual);
    }

    private Dashboard makeTeamDashboard(String template, String title, String appName, String owner, String... compNames) {
        Application app = new Application(appName);
        for (String compName : compNames) {
            app.addComponent(new Component(compName));
        }

        Dashboard dashboard = new Dashboard(template, title, app, owner, DashboardType.Team);
        return dashboard;
    }

    private Widget makePipelineWidget(String devName, String qaName, String intName, String perfName, String prodName){
        Widget pipelineWidget = new Widget();
        pipelineWidget.setName("pipeline");
        Map<String, String> environmentMap = new HashMap<>();

        if(devName != null){
            environmentMap.put(PipelineStageType.Dev.name(), devName);
        }
        if(qaName != null) {
            environmentMap.put(PipelineStageType.QA.name(), qaName);
        }
        if(intName != null) {
            environmentMap.put(PipelineStageType.Int.name(), intName);
        }
        if(perfName != null) {
            environmentMap.put(PipelineStageType.Perf.name(), perfName);
        }
        if(prodName != null) {
            environmentMap.put(PipelineStageType.Prod.name(), prodName);
        }

        pipelineWidget.getOptions().put("mappings", environmentMap);
        return pipelineWidget;
    }
}
