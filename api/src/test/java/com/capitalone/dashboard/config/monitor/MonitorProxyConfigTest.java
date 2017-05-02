package com.capitalone.dashboard.config.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.model.monitor.MonitorProxySettings;

@RunWith(MockitoJUnitRunner.class)
public class MonitorProxyConfigTest {

	private static final String DEFAULT_HOST = "some.company.proxy.host";
	private static final String DEFAULT_TYPE = "HTTP";
	private static final int DEFAULT_PORT = 80;
	private static final String DEFAULT_USERNAME = "username";
	private static final String DEFAULT_PASSWORD = "password";
	
	@Mock
	private MonitorProxySettings monitorProxySettings;
	
	@Spy
	@InjectMocks
	private MonitorProxyConfig monitorProxyConfig;
	
	
	/**
	 * Before and After methods to ensure that the Authenticator is cleared. Tests check
	 * if Authenticator is overridden, must be cleaned out prior to each execution.
	 */
	@Before
	public void setup() {
		Authenticator.setDefault(null);
	}
	
	@After
	public void tearDown() {
		Authenticator.setDefault(null);
	}
	
	@Test
	public void testProxy_PROXY_HTTP() throws Exception {
		when(monitorProxySettings.getUsername()).thenReturn(DEFAULT_USERNAME);
		when(monitorProxySettings.getPassword()).thenReturn(DEFAULT_PASSWORD);
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPort()).thenReturn(DEFAULT_PORT);
		when(monitorProxySettings.getType()).thenReturn(DEFAULT_TYPE);
		
		Proxy expected = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
		Proxy result = monitorProxyConfig.proxy();
		
		assertEquals(expected, result);
		
		verify(monitorProxySettings).getType();
		verify(monitorProxySettings, atLeastOnce()).getHost();
		verify(monitorProxySettings).getPort();
		verify(monitorProxySettings).getUsername();
		verify(monitorProxySettings).getPassword();
		
		assertAuthenticator(ProxyAuthenticator.class);
	}
	
	@Test
	public void testProxy_PROXY_HTTP_lowercase() throws Exception {
		when(monitorProxySettings.getUsername()).thenReturn(DEFAULT_USERNAME);
		when(monitorProxySettings.getPassword()).thenReturn(DEFAULT_PASSWORD);
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPort()).thenReturn(DEFAULT_PORT);
		when(monitorProxySettings.getType()).thenReturn("http");
		
		Proxy expected = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
		Proxy result = monitorProxyConfig.proxy();
		
		assertEquals(expected, result);
		
		verify(monitorProxySettings).getType();
		verify(monitorProxySettings, atLeastOnce()).getHost();
		verify(monitorProxySettings).getPort();
		verify(monitorProxySettings).getUsername();
		verify(monitorProxySettings).getPassword();
		
		assertAuthenticator(ProxyAuthenticator.class);
	}
	
	@Test
	public void testProxy_PROXY_DIRECT() throws Exception {
		when(monitorProxySettings.getUsername()).thenReturn(DEFAULT_USERNAME);
		when(monitorProxySettings.getPassword()).thenReturn(DEFAULT_PASSWORD);
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPort()).thenReturn(DEFAULT_PORT);
		when(monitorProxySettings.getType()).thenReturn("DIRECT");
		
		Proxy expected = Proxy.NO_PROXY;
		Proxy result = monitorProxyConfig.proxy();
		
		assertEquals(expected, result);
		
		verify(monitorProxySettings).getType();
		verify(monitorProxySettings, atLeastOnce()).getHost();
		verify(monitorProxySettings, never()).getPort();
		verify(monitorProxySettings, never()).getUsername();
		verify(monitorProxySettings, never()).getPassword();
		
		assertAuthenticator(null);
	}
	
	@Test
	public void testProxy_PROXY_SOCKS() throws Exception {
		when(monitorProxySettings.getUsername()).thenReturn(DEFAULT_USERNAME);
		when(monitorProxySettings.getPassword()).thenReturn(DEFAULT_PASSWORD);
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPort()).thenReturn(DEFAULT_PORT);
		when(monitorProxySettings.getType()).thenReturn("SOCKS");
		
		Proxy expected = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
		Proxy result = monitorProxyConfig.proxy();
		
		assertEquals(expected, result);
		
		verify(monitorProxySettings).getType();
		verify(monitorProxySettings, atLeastOnce()).getHost();
		verify(monitorProxySettings).getPort();
		verify(monitorProxySettings).getUsername();
		verify(monitorProxySettings).getPassword();
		
		assertAuthenticator(ProxyAuthenticator.class);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testProxy_PROXY_invalid() {
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getType()).thenReturn("NOTAPROXYTYPE");
		monitorProxyConfig.proxy();
		
		fail();
	}
	
	@Test
	public void testProxy_PROXY_empty() throws Exception {
		when(monitorProxySettings.getUsername()).thenReturn(DEFAULT_USERNAME);
		when(monitorProxySettings.getPassword()).thenReturn(DEFAULT_PASSWORD);
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPort()).thenReturn(DEFAULT_PORT);
		when(monitorProxySettings.getType()).thenReturn("");
		
		Proxy expected = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
		Proxy result = monitorProxyConfig.proxy();
		
		assertEquals(expected, result);
		
		verify(monitorProxySettings).getType();
		verify(monitorProxySettings, atLeastOnce()).getHost();
		verify(monitorProxySettings).getPort();
		verify(monitorProxySettings).getUsername();
		verify(monitorProxySettings).getPassword();
		
		assertAuthenticator(ProxyAuthenticator.class);
	}
	
	@Test
	public void testProxy_PROXY_whitespace() throws Exception {
		when(monitorProxySettings.getUsername()).thenReturn(DEFAULT_USERNAME);
		when(monitorProxySettings.getPassword()).thenReturn(DEFAULT_PASSWORD);
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPort()).thenReturn(DEFAULT_PORT);
		when(monitorProxySettings.getType()).thenReturn(" ");
		
		Proxy expected = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
		Proxy result = monitorProxyConfig.proxy();
		
		assertEquals(expected, result);
		
		verify(monitorProxySettings).getType();
		verify(monitorProxySettings, atLeastOnce()).getHost();
		verify(monitorProxySettings).getPort();
		verify(monitorProxySettings).getUsername();
		verify(monitorProxySettings).getPassword();
		
		assertAuthenticator(ProxyAuthenticator.class);
	}
	
	@Test
	public void testProxy_PROXY_null() throws Exception {
		when(monitorProxySettings.getUsername()).thenReturn(DEFAULT_USERNAME);
		when(monitorProxySettings.getPassword()).thenReturn(DEFAULT_PASSWORD);
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPort()).thenReturn(DEFAULT_PORT);
		when(monitorProxySettings.getType()).thenReturn(null);
		
		Proxy expected = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
		Proxy result = monitorProxyConfig.proxy();
		
		assertEquals(expected, result);
		
		verify(monitorProxySettings).getType();
		verify(monitorProxySettings, atLeastOnce()).getHost();
		verify(monitorProxySettings).getPort();
		verify(monitorProxySettings).getUsername();
		verify(monitorProxySettings).getPassword();
		
		assertAuthenticator(ProxyAuthenticator.class);
	}

	@Test
	public void testProxy_NO_PROXY_empty() {
		when(monitorProxySettings.getHost()).thenReturn("");
		assertEquals(Proxy.NO_PROXY, monitorProxyConfig.proxy());
		
		verify(monitorProxySettings, never()).getType();
	}
	
	@Test
	public void testProxy_NO_PROXY_whitespace() {
		when(monitorProxySettings.getHost()).thenReturn(" ");
		assertEquals(Proxy.NO_PROXY, monitorProxyConfig.proxy());
		
		verify(monitorProxySettings, never()).getType();
	}
	
	@Test
	public void testProxy_NO_PROXY_null() {
		when(monitorProxySettings.getHost()).thenReturn(null);
		assertEquals(Proxy.NO_PROXY, monitorProxyConfig.proxy());
		
		verify(monitorProxySettings, never()).getType();
	}
	
	@Test
	public void testAuthenticator_PROXY() {
		PasswordAuthentication expected = new PasswordAuthentication(DEFAULT_USERNAME, DEFAULT_PASSWORD.toCharArray());
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getUsername()).thenReturn(DEFAULT_USERNAME);
		when(monitorProxySettings.getPassword()).thenReturn(DEFAULT_PASSWORD);
		
		PasswordAuthentication result = ((ProxyAuthenticator) monitorProxyConfig.authenticator()).getPasswordAuthentication();
		assertEquals(expected.getUserName(), result.getUserName());
		assertEquals(String.valueOf(expected.getPassword()), String.valueOf(result.getPassword()));
		
		verify(monitorProxyConfig).proxyPasswordAuthentication();
	}
	
	@Test
	public void testAuthenticator_NO_PROXY_empty() {
		when(monitorProxySettings.getHost()).thenReturn("");
		Authenticator preResult = monitorProxyConfig.authenticator();
		assertEquals(ProxyAuthenticator.class, preResult.getClass());
		
		ProxyAuthenticator result = (ProxyAuthenticator) preResult;
		assertNull(result.getPasswordAuthentication());
	}
	
	@Test
	public void testPasswordAuthentication_PROXY() {
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getUsername()).thenReturn(DEFAULT_USERNAME);
		when(monitorProxySettings.getPassword()).thenReturn(DEFAULT_PASSWORD);
		PasswordAuthentication expected = new PasswordAuthentication(DEFAULT_USERNAME, DEFAULT_PASSWORD.toCharArray());
		PasswordAuthentication result = monitorProxyConfig.proxyPasswordAuthentication();
		
		assertEquals(expected.getUserName(), result.getUserName());
		assertEquals(String.valueOf(expected.getPassword()), String.valueOf(result.getPassword()));
		
		verify(monitorProxySettings).getHost();
		verify(monitorProxySettings).getUsername();
		verify(monitorProxySettings).getPassword();
	}
	
	@Test
	public void testPasswordAuthentication_NO_PROXY_empty() {
		when(monitorProxySettings.getHost()).thenReturn("");
		assertNull(monitorProxyConfig.proxyPasswordAuthentication());
		
		verify(monitorProxySettings, never()).getUsername();
		verify(monitorProxySettings, never()).getPassword();
	}
	
	@Test
	public void testPasswordAuthentication_NO_PROXY_whitespace() {
		when(monitorProxySettings.getHost()).thenReturn(" ");
		assertNull(monitorProxyConfig.proxyPasswordAuthentication());
		
		verify(monitorProxySettings, never()).getUsername();
		verify(monitorProxySettings, never()).getPassword();
	}
	
	@Test
	public void testPasswordAuthentication_NO_PROXY_null() {
		when(monitorProxySettings.getHost()).thenReturn(null);
		assertNull(monitorProxyConfig.proxyPasswordAuthentication());
		assertNull(monitorProxyConfig.proxyPasswordAuthentication());
		
		verify(monitorProxySettings, never()).getUsername();
		verify(monitorProxySettings, never()).getPassword();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPasswordAuthentication_PROXY_NO_USERNAME_empty() {
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getUsername()).thenReturn("");
		
		monitorProxyConfig.proxyPasswordAuthentication();
		fail();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPasswordAuthentication_PROXY_NO_USERNAME_whitespace() {
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getUsername()).thenReturn(" ");
		
		monitorProxyConfig.proxyPasswordAuthentication();
		fail();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPasswordAuthentication_PROXY_NO_USERNAME_null() {
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getUsername()).thenReturn(null);
		
		monitorProxyConfig.proxyPasswordAuthentication();
		fail();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPasswordAuthentication_PROXY_NO_PASSWORD_empty() {
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPassword()).thenReturn("");
		
		monitorProxyConfig.proxyPasswordAuthentication();
		fail();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPasswordAuthentication_PROXY_NO_PASSWORD_whitespace() {
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPassword()).thenReturn(" ");
		
		monitorProxyConfig.proxyPasswordAuthentication();
		fail();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPasswordAuthentication_PROXY_NO_PASSWORD_null() {
		when(monitorProxySettings.getHost()).thenReturn(DEFAULT_HOST);
		when(monitorProxySettings.getPassword()).thenReturn(null);
		
		monitorProxyConfig.proxyPasswordAuthentication();
		fail();
	}
	
	private void assertAuthenticator(Class<ProxyAuthenticator> clazz) throws Exception {
		Field field = Authenticator.class.getDeclaredField("theAuthenticator");
        field.setAccessible(true);
        Object result = field.get(null);
		
        if(clazz == null) {
        	assertNull(result);
        } else {     
        	assertEquals(clazz, result.getClass());
        }
	}
}
