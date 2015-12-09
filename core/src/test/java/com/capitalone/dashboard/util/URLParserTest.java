package com.capitalone.dashboard.util;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Created by yaf107 on 12/9/15.
 */
public class URLParserTest {


    @Test
    public void testOne() {
        String userName = null;
        String token = null;
        URL url = null;
        String testurl = "john:johns1234!password@http://mycompany.jenkins.com/jenkinsforme/";
        try {
            URLParser parser = new URLParser(testurl);
            userName = parser.getUserName();
            token = parser.getToken();
            url = parser.getUrl();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertEquals("john", userName);
        assertEquals("johns1234!password", token);
        assertEquals("mycompany.jenkins.com", url.getHost());
        assertEquals("/jenkinsforme/", url.getPath());
        assertEquals("http", url.getProtocol());
    }

    @Test
    public void testOneHTTPS() {
        String userName = null;
        String token = null;
        URL url = null;
        String testurl = "john:johns1234!password@https://mycompany.jenkins.com/jenkinsforme/";
        try {
            URLParser parser = new URLParser(testurl);
            userName = parser.getUserName();
            token = parser.getToken();
            url = parser.getUrl();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertEquals("john", userName);
        assertEquals("johns1234!password", token);
        assertEquals("mycompany.jenkins.com", url.getHost());
        assertEquals("/jenkinsforme/", url.getPath());
        assertEquals("https", url.getProtocol());
    }

    @Test
    public void testTwo() {
        String userName = null;
        String token = null;
        URL url = null;
        String testurl = "john@http://mycompany.jenkins.com/jenkinsforme/";
        try {
            URLParser parser = new URLParser(testurl);
            userName = parser.getUserName();
            token = parser.getToken();
            url = parser.getUrl();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertEquals("john", userName);
        assertEquals("", token);
        assertEquals("mycompany.jenkins.com", url.getHost());
        assertEquals("/jenkinsforme/", url.getPath());
        assertEquals("http", url.getProtocol());
    }

    @Test
    public void testTwoHTTPS() {
        String userName = null;
        String token = null;
        URL url = null;
        String testurl = "john@https://mycompany.jenkins.com/jenkinsforme/";
        try {
            URLParser parser = new URLParser(testurl);
            userName = parser.getUserName();
            token = parser.getToken();
            url = parser.getUrl();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertEquals("john", userName);
        assertEquals("", token);
        assertEquals("mycompany.jenkins.com", url.getHost());
        assertEquals("/jenkinsforme/", url.getPath());
        assertEquals("https", url.getProtocol());
    }

    @Test
    public void testThree() {
        String userName = null;
        String token = null;
        String testurl = "john:@http://mycompany.jenkins.com/jenkinsforme/";
        try {
            URLParser parser = new URLParser(testurl);
            userName = parser.getUserName();
            token = parser.getToken();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertEquals("john", userName);
        assertEquals("", token);
    }

    @Test(expected = MalformedURLException.class)
    public void testFour() throws Exception {
        String testurl = "john:johnsManyPassword:Another123password@http://mycompany.jenkins.com/jenkinsforme/";
        @SuppressWarnings("unused")
        URLParser parser = new URLParser(testurl);
    }


}