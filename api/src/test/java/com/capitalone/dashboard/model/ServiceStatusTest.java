package com.capitalone.dashboard.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ServiceStatusTest {

	@Test
	public void testStatuses() {
		assertEquals(ServiceStatus.Ok, ServiceStatus.fromString("ok"));
		assertEquals(ServiceStatus.Warning, ServiceStatus.fromString("warning"));
		assertEquals(ServiceStatus.Unauth, ServiceStatus.fromString("unauth"));
		assertEquals(ServiceStatus.Alert, ServiceStatus.fromString("alert"));
	}
	
	@Test
	public void testStatuses_capital() {
		assertEquals(ServiceStatus.Ok, ServiceStatus.fromString("OK"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBadStatus() {
		ServiceStatus.fromString("THISISABADSTATUS");
	}
	
	@Test
	public void testOK() {
		assertEquals(ServiceStatus.Ok, ServiceStatus.getServiceStatus(200));
	}
	
	@Test
	public void testWARN1() {
		assertEquals(ServiceStatus.Warning, ServiceStatus.getServiceStatus(300));
	}
	
	@Test
	public void testWARN2() {
		assertEquals(ServiceStatus.Warning, ServiceStatus.getServiceStatus(350));
	}
	
	@Test
	public void testWARN3() {
		assertEquals(ServiceStatus.Warning, ServiceStatus.getServiceStatus(400));
	}
	
	@Test
	public void testUNAUTH() {
		assertEquals(ServiceStatus.Unauth, ServiceStatus.getServiceStatus(401));
	}
	
	@Test
	public void testALERT1() {
		assertEquals(ServiceStatus.Alert, ServiceStatus.getServiceStatus(1));
	}
	
	@Test
	public void testALERT2() {
		assertEquals(ServiceStatus.Alert, ServiceStatus.getServiceStatus(199));
	}
	
	@Test
	public void testALERT3() {
		assertEquals(ServiceStatus.Alert, ServiceStatus.getServiceStatus(402));
	}
	
	@Test
	public void testALERT4() {
		assertEquals(ServiceStatus.Alert, ServiceStatus.getServiceStatus(500));
	}
	
}
