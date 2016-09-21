package com.capitalone.dashboard.util;


import org.apache.commons.beanutils.BeanUtilsBean;
import org.jboss.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

public class HygieiaUtils {
	private static final Logger LOGGER = Logger.getLogger(HygieiaUtils.class);

    public static void mergeObjects(Object dest, Object source) throws IllegalAccessException, InvocationTargetException {
        new BeanUtilsBean() {
            @Override
            public void copyProperty(Object dest, String name, Object value)
                    throws IllegalAccessException, InvocationTargetException {
                if (value != null) {
                    super.copyProperty(dest, name, value);
                }
            }
        }.copyProperties(dest, source);
    }
    
    @SuppressWarnings("PMD.NPathComplexity")
    public static boolean smartUrlEquals(String url1, String url2) {
    	String u1 = nullSafe(url1);
    	String u2 = nullSafe(url2);
    	
    	String u1Host = null;
    	String u1Path = null;
    	String u1Query = null;
    	
    	String u2Host = null;
    	String u2Path = null;
    	String u2Query = null;
    	try {
    		if (u1.length() > 0) {
    			URL url = new URL(u1);
    			u1Host = url.getHost();
    			u1Path = url.getPath();
    			u1Query = url.getQuery();
    		}
    	} catch (MalformedURLException e) {
    		LOGGER.warn("Invalid Url " + e.getMessage(), e);
    	}
    	u1Host = nullSafe(u1Host);
    	u1Path = nullSafe(u1Path);
    	u1Query = nullSafe(u1Query);
    	
    	try {
    		if (u2.length() > 0) {
    			URL url = new URL(u2);
    			u2Host = url.getHost();
    			u2Path = url.getPath();
    			u2Query = url.getQuery();
    		}
    	} catch (MalformedURLException e) {
    		LOGGER.warn("Invalid Url " + e.getMessage(), e);
    	}
    	
    	u2Host = nullSafe(u2Host);
    	u2Path = nullSafe(u2Path);
    	u2Query = nullSafe(u2Query);
    	
    	if (u1Path.endsWith(".git")) {
    		u1Path = u1Path.substring(0, u1Path.length() - 4);
    	}
    	
    	if (u2Path.endsWith(".git")) {
    		u2Path = u2Path.substring(0, u2Path.length() - 4);
    	}
    	
    	// TODO find a better way to handle load balancers
    	String u1PrimaryDomain = u1Host;
    	String u2PrimaryDomain = u2Host;
    	
    	int idx;
    	idx = u1Host.lastIndexOf('.');
    	if (idx > 0) {
    		idx = u1Host.lastIndexOf('.', idx - 1);
    	}
    	if (idx >= 0) {
    		u1PrimaryDomain = u1Host.substring(idx);
    	}
    	
    	idx = u2Host.lastIndexOf('.');
    	if (idx > 0) {
    		idx = u2Host.lastIndexOf('.', idx - 1);
    	}
    	if (idx >= 0) {
    		u2PrimaryDomain = u2Host.substring(idx);
    	}
    	
    	return safeEquals(u1PrimaryDomain, u2PrimaryDomain)
    			&& safeEquals(u1Path, u2Path)
    			&& safeEquals(u1Query, u2Query);
    }
    
    private static String nullSafe(String str) {
    	return str == null? "" : str;
    }
    
    private static boolean safeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }
}
