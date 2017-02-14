package com.capitalone.dashboard.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import com.capitalone.dashboard.request.DashboardRequest;

public class DashboardRequestTest {

	private static String TITLE = "title";
	private static String APP_NAME = "app_name";
	private static String COMP_NAME = "comp_name";
	private static String OWNER = "owner";
	private static String TYPE = "Team";

	private DashboardRequest request;

	private Map<WidgetFamily, List<WidgetType>> activeWidgetTypes;
	private List<Widget> widgets;
	
	@Before
	public void init() {
		request = new DashboardRequest();

		activeWidgetTypes = new HashMap<WidgetFamily, List<WidgetType>>();

		List<WidgetType> widget = new ArrayList<WidgetType>();
		widget.add(WidgetType.codeanalysis);
		List<WidgetType> cloud = new ArrayList<WidgetType>();
		cloud.add(WidgetType.cloud);
		List<WidgetType> pipeline = new ArrayList<WidgetType>();
		pipeline.add(WidgetType.pipeline);

		activeWidgetTypes.put(WidgetFamily.widget, widget);
		activeWidgetTypes.put(WidgetFamily.cloud, cloud);
		activeWidgetTypes.put(WidgetFamily.pipeline, pipeline);
		
		widgets = new ArrayList<Widget>();
		widgets.add(new Widget());
		
		request.setTitle(TITLE);
		request.setApplicationName(APP_NAME);
		request.setComponentName(COMP_NAME);
		request.setOwner(OWNER);
		request.setType(TYPE);
		request.setActiveWidgetTypes(activeWidgetTypes);
		request.setWidgets(widgets);
	}
	
	@Test
	public void testToDashboard() {
		Dashboard result = request.toDashboard();
		
		assertEquals(TITLE, result.getTitle());
		assertEquals(APP_NAME, result.getApplication().getName());
		assertEquals(OWNER, result.getOwner());
		assertEquals(TYPE, result.getType().name());
		assertEquals(activeWidgetTypes, result.getActiveWidgetTypes());
		assertEquals(widgets, result.getWidgets());
	}

	@Test
	public void testCopyTo() {
		Dashboard dash = request.toDashboard();
		dash.setTitle("somethingelse");
		
		assertNull(dash.getId());
		assertEquals("somethingelse", dash.getTitle());
		
		dash.setId(new ObjectId());
		
		Dashboard result = request.copyTo(dash);
		assertEquals(TITLE, result.getTitle());
		assertEquals(APP_NAME, result.getApplication().getName());
		assertEquals(OWNER, result.getOwner());
		assertEquals(TYPE, result.getType().name());
		assertEquals(dash.getId(), result.getId());
		assertEquals(activeWidgetTypes, result.getActiveWidgetTypes());
		assertEquals(widgets, result.getWidgets());
		
	}
}
