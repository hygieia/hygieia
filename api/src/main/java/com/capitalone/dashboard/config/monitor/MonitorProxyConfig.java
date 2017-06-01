package com.capitalone.dashboard.config.monitor;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.capitalone.dashboard.model.monitor.MonitorProxySettings;

@Configuration
public class MonitorProxyConfig {

	@Autowired
	private MonitorProxySettings monitorProxySettings;
	
	@Bean
	public Proxy proxy() {
		Proxy proxy = Proxy.NO_PROXY;
		
		String host = monitorProxySettings.getHost();
		if(StringUtils.isNotBlank(host)) {
			String type = monitorProxySettings.getType();
			Proxy.Type proxyType = StringUtils.isBlank(type) ? Proxy.Type.HTTP : Proxy.Type.valueOf(type.toUpperCase(Locale.ENGLISH));
			
			if(proxyType == Proxy.Type.DIRECT) {
				return proxy;
			}
    		
    		proxy = new Proxy(proxyType, new InetSocketAddress(host, monitorProxySettings.getPort()));
            Authenticator.setDefault(authenticator());	
		}
		
		return proxy;
	}
	
	@Bean
	public Authenticator authenticator() {
		return new ProxyAuthenticator(proxyPasswordAuthentication());
	}
	
	@Bean
	public PasswordAuthentication proxyPasswordAuthentication() {
		if(StringUtils.isNotBlank(monitorProxySettings.getHost())) {
			String username = monitorProxySettings.getUsername();
			String password = monitorProxySettings.getPassword();
			
			if(StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
				throw new IllegalArgumentException("When enabling proxy access for the Monitor widget, username and password is required. Please ensure monitor.proxy.username and monitor.proxy.password are supplied.");
			}
			
			return new PasswordAuthentication(username, password.toCharArray());
		}
		
		return null;
	}
}
