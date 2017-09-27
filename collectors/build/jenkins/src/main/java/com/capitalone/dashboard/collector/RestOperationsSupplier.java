package com.capitalone.dashboard.collector;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import com.capitalone.dashboard.util.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Supplier that returns an instance of RestOperations
 */
@Component
public class RestOperationsSupplier implements Supplier<RestOperations> {
	
	private final HudsonSettings settings;
	
    @Autowired
    public RestOperationsSupplier(HudsonSettings settings) {
        this.settings = settings;
    }
    
    @Override
    public RestOperations get() {
        if (StringUtils.isNotEmpty(this.settings.getProxy())) {
        	String proxy = this.settings.getProxy();
        	String[] proxyArray = proxy.split(":");
        	SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            
            if (proxyArray.length == 1) {
            	requestFactory.setProxy(new Proxy(Type.HTTP, new InetSocketAddress(proxyArray[0], 80)));
			}else {
				requestFactory.setProxy(new Proxy(Type.HTTP, new InetSocketAddress(proxyArray[0], Integer.parseInt(proxyArray[1]))));
			}
            return new RestTemplate(requestFactory);
		}else {
			return new RestTemplate();
		}
    }
}
