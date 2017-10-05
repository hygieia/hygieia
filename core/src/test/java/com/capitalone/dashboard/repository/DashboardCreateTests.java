
package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.Cmdb;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class DashboardCreateTests extends FongoBaseRepositoryTest {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private CmdbRepository cmdbRepository;

    @Test
    public void createTeamDashboardTest() {
        Component component = new Component("Jay's component");
        component.setOwner("Jay");

        component = componentRepository.save(component);
        System.out.println(component.getId());

        Cmdb configItemApp = new Cmdb();
        configItemApp.setConfigurationItem("ASVTEST");
        configItemApp = cmdbRepository.save(configItemApp);
        configItemApp.getId();

        Cmdb configItemComp = new Cmdb();
        configItemComp.setConfigurationItem("BAPTEST");
        configItemComp = cmdbRepository.save(configItemComp);
        configItemComp.getId();

        Application application = new Application("Jay's App", component);

        List<String> activeWidgets = new ArrayList<>();
        Dashboard dashboard = new Dashboard("Topo", "Jays's Dashboard", application, new Owner("amit", AuthType.STANDARD), DashboardType.Team,  configItemApp.getId(), configItemComp.getId(),activeWidgets);

        Widget build = new Widget();
        build.setName("build");
        build.getOptions().put("color", "red");
        build.getOptions().put("items", new String[] { "item 1", "item 2"});
        dashboard.getWidgets().add(build);

        Widget scm = new Widget();
        scm.setName("scm");
        scm.getOptions().put("enabled", true);
        scm.getOptions().put("foo", "bar");
        scm.getOptions().put("threshold", 10);
        dashboard.getWidgets().add(scm);


        dashboardRepository.save(dashboard);



        for (Dashboard d : dashboardRepository.findAll(new Sort(Sort.Direction.ASC, "title"))) {
            System.out.println(d.getTitle());
            assertEquals(d.getTitle(), "Jays's Dashboard");
        }

    }
}
