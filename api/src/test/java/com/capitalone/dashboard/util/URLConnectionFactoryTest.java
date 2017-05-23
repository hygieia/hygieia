package com.capitalone.dashboard.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class URLConnectionFactoryTest {

	private URLConnectionFactory factory;

	@Mock
	private HttpURLConnection mockUrlCon;
	
	private Proxy proxy;
	private boolean flag = false;
	@Before
	public void setup() {
		proxy = Proxy.NO_PROXY;
		factory = new URLConnectionFactory(proxy);
	}
	
	@Test
	public void testGet() throws Exception {
		URL url = getStubUrl();
		HttpURLConnection result = factory.get(url);
		
		verify(result).setRequestMethod("GET");
		verify(result).setConnectTimeout(5000);
		
		assertTrue(flag);
	}
	
	private URL getStubUrl() throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(
		        "<html></html>".getBytes("UTF-8"));
		doReturn(is).when(mockUrlCon).getInputStream();

		URLStreamHandler stubUrlHandler = spy(new URLStreamHandler() {
		    @Override
		     protected URLConnection openConnection(URL u) throws IOException {
		        return mockUrlCon;
		     }    
		    
		    @Override
		    protected URLConnection openConnection(URL u, Proxy p) {
		    	// signify that the method was hit
		    	flag = true;
		    	assertEquals(proxy, p);
		    	return mockUrlCon;
		    }
		});
		return new URL("foo", "bar", 99, "/foobar", stubUrlHandler);
	}
	
}
