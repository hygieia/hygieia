package com.capitalone.dashboard.fixture;
 
 import java.util.ArrayList;
 import java.util.List;

 import com.capitalone.dashboard.model.*;
 import com.capitalone.dashboard.request.DashboardRemoteRequest;
 import org.bson.types.ObjectId;

 import com.capitalone.dashboard.request.DashboardRequest;

 public class DashboardFixture {

 	public static DashboardRequest makeDashboardRequest(String template, String title, String appName, String compName,
 			String owner, List<String> teamDashboardIds, String type, String configItemAppName, String configItemComponentName) {
 		DashboardRequest request = new DashboardRequest();
 		request.setTemplate(template);
 		request.setTitle(title);
 		request.setApplicationName(appName);
 		request.setComponentName(compName);
 		request.setConfigurationItemBusServName(configItemAppName);
 		request.setConfigurationItemBusAppName(configItemComponentName);
 		request.setType(type);

 		return request;
 	}

	 public static DashboardRemoteRequest makeDashboardRemoteRequest(String template, String title, String appName, String compName,
														 String owner, List<String> teamDashboardIds, String type, String configItemAppName, String configItemComponentName) {
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
		 metaData.setBusinessApplication(configItemComponentName);
		 metaData.setBusinessService(configItemAppName);
		 request.setMetaData(metaData);


		 return request;
	 }

 	public static Dashboard makeDashboard(String template, String title, String appName, String compName, String owner,
 			DashboardType type, String configItemAppName, String configItemComponentName) {
 		Application application = null;
 		if (type.equals(DashboardType.Team)) {
 			Component component = new Component();
 			component.setName(compName);
 			application = new Application(appName, component);
 		}
		List<String> activeWidgets = new ArrayList<>();
		List<Owner> owners = new ArrayList<>();
		owners.add(new Owner(owner, AuthType.STANDARD));
		return new Dashboard(template, title, application, owners, type,configItemAppName, configItemComponentName,activeWidgets, false, ScoreDisplayType.HEADER);
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
