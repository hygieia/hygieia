package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capitalone.dashboard.config.MongoConfig;

@ContextConfiguration(classes={ MongoConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class DashboardCreateTests {


    @ClassRule
    public static final EmbeddedMongoDBRule RULE = new EmbeddedMongoDBRule();

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Test
    public void createTeamDashboardTest() {
        Component component = new Component("Jay's component");
        component.setOwner("Jay");

        component = componentRepository.save(component);
        System.out.println(component.getId());

        Application application = new Application("Jay's App", component);

        Dashboard dashboard = new Dashboard("Topo", "Jays's Dashboard", application,"amit", DashboardType.Team);

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
        }

    }
}
