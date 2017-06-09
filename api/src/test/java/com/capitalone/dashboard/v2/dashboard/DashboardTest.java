package com.capitalone.dashboard.v2.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;

public class DashboardTest {

    @Test
    public void testContstuctorNoId() {
        String template = "template";
        String title = "title";
        Application application = new Application("name");
        HashSet<Owner> owners = new HashSet<>();
        DashboardType team = DashboardType.Team;
        com.capitalone.dashboard.model.Dashboard oldDash = new com.capitalone.dashboard.model.Dashboard(template, title, application, owners, team);
       
        Dashboard dashboard = new Dashboard(oldDash);
        
        assertEquals(application, dashboard.getApplication());
        assertEquals(template, dashboard.getTemplate());
        assertEquals(title, dashboard.getTitle());
        assertEquals(owners, dashboard.getOwners());
        assertEquals(team, dashboard.getType());
        assertNull(dashboard.getId());
    }
    
    @Test
    public void testContstuctorWithId() {
        String template = "template";
        String title = "title";
        Application application = new Application("name");
        HashSet<Owner> owners = new HashSet<>();
        DashboardType team = DashboardType.Team;
        com.capitalone.dashboard.model.Dashboard oldDash = new com.capitalone.dashboard.model.Dashboard(template, title, application, owners, team);
        ObjectId id = new ObjectId();
        oldDash.setId(id);
        
        Dashboard dashboard = new Dashboard(oldDash);
        
        assertEquals(application, dashboard.getApplication());
        assertEquals(template, dashboard.getTemplate());
        assertEquals(title, dashboard.getTitle());
        assertEquals(owners, dashboard.getOwners());
        assertEquals(team, dashboard.getType());
        assertEquals(id.toHexString(), dashboard.getDashboardId());
    }
    
    @Test
    public void toDomainModel() {
        String template = "template";
        String title = "title";
        Application application = new Application("name");
        HashSet<Owner> owners = new HashSet<>();
        DashboardType team = DashboardType.Team;
        ObjectId id = new ObjectId();
        Dashboard dashboard = new Dashboard();
        dashboard.setTemplate(template);
        dashboard.setTitle(title);
        dashboard.setApplication(application);
        dashboard.setOwners(owners);
        dashboard.setType(team);
        dashboard.setDashboardId(id.toHexString());
        
        com.capitalone.dashboard.model.Dashboard result = dashboard.toDomainModel();
        
        assertEquals(application, result.getApplication());
        assertEquals(template, result.getTemplate());
        assertEquals(title, result.getTitle());
        assertEquals(owners, result.getOwners());
        assertEquals(team, result.getType());
        assertEquals(id, result.getId());
    }
    
    

}
