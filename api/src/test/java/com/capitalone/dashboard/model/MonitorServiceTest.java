package com.capitalone.dashboard.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.model.monitor.MonitorService;

@RunWith(MockitoJUnitRunner.class)
public class MonitorServiceTest {

	private static final String URL = "http://www.google.com";
	
	private MonitorService service;
	private boolean connectFlag;
	private boolean throwError;
	
	@Before
	public void setup() throws MalformedURLException {
		service = new MonitorService(new TestConnection(new URL(URL)), new ObjectId());
		connectFlag = false;
		throwError = false;
	}
	
	@Test
	public void testPositive() {
		assertEquals(ServiceStatus.Ok, service.getServiceStatus());
		assertTrue(connectFlag);
	}
	
	@Test
	public void testNegative() {
		throwError = true;
		assertEquals(ServiceStatus.Alert, service.getServiceStatus());
		assertTrue(connectFlag);
	}
	
	class TestConnection extends HttpURLConnection {

		protected TestConnection(URL u) {
			super(u);
		}
		
		@Override
		public int getResponseCode() throws IOException {
			if(throwError) {
				throw new IOException();
			}
			return 200;
		}

		@Override
		public void disconnect() {
			
		}

		@Override
		public boolean usingProxy() {
			return false;
		}

		@Override
		public void connect() throws IOException {
			connectFlag = true;
		}
		
	}
	
}
