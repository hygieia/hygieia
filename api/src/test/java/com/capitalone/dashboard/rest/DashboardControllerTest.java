package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.request.DashboardRequest;
import com.capitalone.dashboard.request.WidgetRequest;
import com.capitalone.dashboard.service.DashboardService;
import com.capitalone.dashboard.util.TestUtil;
import com.capitalone.dashboard.util.WidgetOptionsBuilder;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class DashboardControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;
    @Autowired private DashboardService dashboardService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void dashboards() throws Exception {
        Dashboard d1 = makeDashboard("t1", "title", "app", "comp","amit", DashboardType.Team);
        when(dashboardService.all()).thenReturn(Arrays.asList(d1));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].template", is("t1")))
                .andExpect(jsonPath("$[0].title", is("title")))
                .andExpect(jsonPath("$[0].application.name", is("app")))
                .andExpect(jsonPath("$[0].application.components[0].name", is("comp")));
    }

    @Test
    public void createProductDashboard() throws Exception {
        DashboardRequest request = makeDashboardRequest("template", "title", null, null,"amit", null, "product");
        mockMvc.perform(post("/dashboard")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void createTeamDashboard() throws Exception {
        DashboardRequest request = makeDashboardRequest("template", "title", "app", "comp","amit", null, "team");
        mockMvc.perform(post("/dashboard")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
            .andExpect(status().isCreated());
    }

    @Test
    public void createDashboard_nothingProvided_badRequest() throws Exception {
        mockMvc.perform(post("/dashboard")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(new DashboardRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.template", hasItems("may not be null")))
            .andExpect(jsonPath("$.fieldErrors.title", hasItems("may not be null")))

//            TODO:  These are no longer necessary in all cases.  Potentially add new class-level validator.
//            .andExpect(jsonPath("$.fieldErrors.componentName", hasItems("may not be null")))
//            .andExpect(jsonPath("$.fieldErrors.applicationName", hasItems("may not be null")))
            ;
    }

    @Test
    public void getDashboard() throws Exception {
        ObjectId objectId = new ObjectId("54b982620364c80a6136c9f2");
        Dashboard d1 = makeDashboard("t1", "title", "app", "comp","amit", DashboardType.Team);
        d1.setId(objectId);

        when(dashboardService.get(objectId)).thenReturn(d1);

        mockMvc.perform(get("/dashboard/" + objectId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(objectId.toString())));
    }

    @Test
    public void updateTeamDashboard() throws Exception {
        ObjectId objectId = new ObjectId("54b982620364c80a6136c9f2");
        Dashboard orig = makeDashboard("t1", "title", "app", "comp","amit", DashboardType.Team);
        DashboardRequest request = makeDashboardRequest("template", "title", "app", "comp","amit", null, "team");

        when(dashboardService.get(objectId)).thenReturn(orig);
        when(dashboardService.update(Matchers.any(Dashboard.class))).thenReturn(orig);

        mockMvc.perform(put("/dashboard/" + objectId.toString())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteDashboard() throws Exception {
        ObjectId objectId = new ObjectId("54b982620364c80a6136c9f2");

        mockMvc.perform(delete("/dashboard/" + objectId.toString())).andExpect(status().isNoContent());
    }

    @Test
    public void addWidget() throws Exception {
        ObjectId dashId = ObjectId.get();
        ObjectId compId = ObjectId.get();
        ObjectId collId = ObjectId.get();
        List<ObjectId> collIds = Arrays.asList(collId);
        Map<String, Object> options = new WidgetOptionsBuilder().put("option1", 1).put("option2", "2").get();
        WidgetRequest request = makeWidgetRequest("build", compId, collIds, options);
        Dashboard d1 = makeDashboard("t1", "title", "app", "comp","amit", DashboardType.Team);
        Widget widgetWithId = request.widget();
        widgetWithId.setId(ObjectId.get());
        Component component = makeComponent(compId, "Component", CollectorType.Build, collId);

        when(dashboardService.get(dashId)).thenReturn(d1);
        when(dashboardService.associateCollectorToComponent(compId, collIds)).thenReturn(component);
        when(dashboardService.addWidget(Matchers.any(Dashboard.class), Matchers.any(Widget.class))).thenReturn(widgetWithId);

        mockMvc.perform(post("/dashboard/" + dashId.toString() + "/widget")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.widget.id", is(widgetWithId.getId().toString())))
                .andExpect(jsonPath("$.widget.name", is("build")))
                .andExpect(jsonPath("$.widget.componentId", is(compId.toString())))
                .andExpect(jsonPath("$.widget.options.option1", is(1)))
                .andExpect(jsonPath("$.widget.options.option2", is("2")))
                .andExpect(jsonPath("$.component.id", is(component.getId().toString())))
                .andExpect(jsonPath("$.component.name", is(component.getName())))
                .andExpect(jsonPath("$.component.collectorItems.Build[0].id", is(collId.toString())))
        ;
    }

    @Test
    public void updateWidget() throws Exception {
        ObjectId dashId = ObjectId.get();
        ObjectId widgetId = ObjectId.get();
        ObjectId compId = ObjectId.get();
        ObjectId collId = ObjectId.get();
        List<ObjectId> collIds = Arrays.asList(collId);
        Map<String, Object> options = new WidgetOptionsBuilder().put("option1", 2).put("option2", "3").get();
        WidgetRequest request = makeWidgetRequest("build", compId, collIds, options);
        Dashboard d1 = makeDashboard("t1", "title", "app", "comp","amit", DashboardType.Team);
        Widget widget = makeWidget(widgetId, "build", compId, options);

        when(dashboardService.get(dashId)).thenReturn(d1);
        when(dashboardService.getWidget(d1, widgetId)).thenReturn(widget);
        when(dashboardService.updateWidget(Matchers.any(Dashboard.class), Matchers.any(Widget.class))).thenReturn(widget);

        mockMvc.perform(put("/dashboard/" + dashId.toString() + "/widget/" + widgetId.toString())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isOk());
    }

    private DashboardRequest makeDashboardRequest(String template, String title, String appName, String compName, String owner, List<String> teamDashboardIds, String type) {
        DashboardRequest request = new DashboardRequest();
        request.setTemplate(template);
        request.setTitle(title);
        request.setApplicationName(appName);
        request.setComponentName(compName);
        request.setOwner(owner);
        request.setType(type);

        return request;
    }

    private Dashboard makeDashboard(String template, String title, String appName, String compName, String owner, DashboardType type) {
        Application application = null;
        if(type.equals(DashboardType.Team)){
            Component component = new Component();
            component.setName(compName);
            application = new Application(appName, component);
        }

        return new Dashboard(template, title, application,owner, type);
    }

    private Component makeComponent(ObjectId id, String name, CollectorType type, ObjectId collItemId) {
        Component c = new Component();
        c.setId(id);
        c.setName(name);

        CollectorItem item = new CollectorItem();
        item.setId(collItemId);

        c.addCollectorItem(type, item);
        return c;
    }

    private Widget makeWidget(ObjectId widgetId, String name, ObjectId compId, Map<String, Object> options) {
        Widget widget = new Widget();
        widget.setId(widgetId);
        widget.setName(name);
        widget.setComponentId(compId);
        widget.getOptions().putAll(options);
        return widget;
    }

    private WidgetRequest makeWidgetRequest(String name, ObjectId componentId,
                                            List<ObjectId> collIds, Map<String, Object> options) {
        WidgetRequest request = new WidgetRequest();
        request.setName(name);
        request.setComponentId(componentId);
        request.setCollectorItemIds(collIds);
        request.setOptions(options);
        return request;
    }
}
