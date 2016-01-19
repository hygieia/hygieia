package com.capitalone.dashboard.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ClearSystemProperties;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ProxySettingsTests {
    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public final ClearSystemProperties proxyCleared = new ClearSystemProperties(
            "http.proxyHost","http.proxyPort", "http.proxyUser", "http.proxyPassword",
            "https.proxyHost","https.proxyPort", "https.proxyUser", "https.proxyPassword");

    @Test
    public void testEnv() throws Exception {
        final AtomicInteger invokes = new AtomicInteger(0);
        ProxySettings p = new ProxySettings("http") {
            String getEnv(String protocol) {
                if (invokes.getAndIncrement() == 0) {
                    assertEquals("HTTP_PROXY", protocol);
                    return null;
                }

                assertEquals("http_proxy", protocol);
                return "http://www.google.com";
            }
        };
        assertEquals("http://www.google.com", p.getEnvProxy("http"));
        assertEquals(2, invokes.get());
    }

    @Test
    public void testHttpProperties() throws Exception {
        System.setProperty("http.proxyHost", "www.google.com");
        ProxySettings proxy = ProxySettings.buildHTTP();
        assertTrue(proxy.isProxySet());

        assertEquals("www.google.com", proxy.host());
        assertEquals("80", proxy.port());
        assertEquals("", proxy.user());
        assertEquals("", proxy.password());
    }

    @Test
    public void testHttpsProperties() throws Exception {
        System.setProperty("https.proxyHost", "www.google.com");
        System.setProperty("https.proxyUser", "person");
        System.setProperty("https.proxyPassword", "secret");

        ProxySettings proxy = ProxySettings.buildHTTPS();
        assertTrue(proxy.isProxySet());

        assertEquals("www.google.com", proxy.host());
        assertEquals("443", proxy.port());
        assertEquals("person", proxy.user());
        assertEquals("secret", proxy.password());
    }

    @Test
    public void testEnvProps() throws Exception {
        final AtomicInteger invokes = new AtomicInteger(0);
        ProxySettings p = new ProxySettings("http") {
            String getEnv(String protocol) {
                if (invokes.getAndIncrement() == 0) {
                    assertEquals("HTTP_PROXY", protocol);
                    return null;
                }

                assertEquals("http_proxy", protocol);
                return "http://user:secret@www.google.com";
            }
        };
        assertTrue(p.isProxySet());
        assertEquals("www.google.com", p.host());
        assertEquals("80", p.port());
        assertEquals("user", p.user());
        assertEquals("secret", p.password());
        assertEquals(2, invokes.get());
    }
}
