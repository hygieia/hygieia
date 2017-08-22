package com.capitalone.dashboard.fixture;
 
 import java.util.ArrayList;
 import java.util.List;

 import com.capitalone.dashboard.request.DashboardRemoteRequest;
 import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.Application;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.request.DashboardRequest;
 
 public class DashboardFixture {
 
 	public static DashboardRequest makeDashboardRequest(String template, String title, String appName, String compName,
 			String owner, List<String> teamDashboardIds, String type, ObjectId configItemAppId, ObjectId configItemComponentId) {
 		DashboardRequest request = new DashboardRequest();
 		request.setTemplate(template);
 		request.setTitle(title);
 		request.setApplicationName(appName);
 		request.setComponentName(compName);
 		request.setConfigurationItemBusServObjectId(configItemAppId);
 		request.setConfigurationItemBusAppObjectId(configItemComponentId);
 		request.setType(type);
 
 		return request;
 	}

	 public static DashboardRemoteRequest makeDashboardRemoteRequest(String template, String title, String appName, String compName,
														 String owner, List<String> teamDashboardIds, String type, ObjectId configItemAppId, ObjectId configItemComponentId) {
		 DashboardRemoteRequest request = new DashboardRemoteRequest();
		 DashboardRemoteRequest.DashboardMetaData metaData = new DashboardRemoteRequest.DashboardMetaData();
		 Owner owner1 = new Owner();
		 owner1.setUsername(owner);
		 owner1.setAuthType(AuthType.STANDARD);
		 metaData.setApplicationName(appName);
		 metaData.setOwner(owner1);
		 metaData.setComponentName(compName);
		 metaData.setTemplate(template);
		 metaData.setTitle(title);
		 metaData.setType(type);
		 request.setMetaData(metaData);


		 return request;
	 }

 	public static Dashboard makeDashboard(String template, String title, String appName, String compName, String owner,
 			DashboardType type, ObjectId configItemAppId, ObjectId configItemComponentId) {
 		Application application = null;
 		if (type.equals(DashboardType.Team)) {
 			Component component = new Component();
 			component.setName(compName);
 			application = new Application(appName, component);
 		}
		List<String> activeWidgets = new ArrayList<>();
		return new Dashboard(template, title, application, new Owner(owner, AuthType.STANDARD), type,configItemAppId, configItemComponentId,activeWidgets);
	}
 
 	public static Component makeComponent(ObjectId id, String name, CollectorType type, ObjectId collItemId) {
 		Component c = new Component();
 		c.setId(id);
 		c.setName(name);
 
 		CollectorItem item = new CollectorItem();
 		item.setId(collItemId);
 
 		c.addCollectorItem(type, item);
 		return c;
 	}
 
 }