package com.capitalone.dashboard.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class URLConnectionFactory {

	private Proxy proxy;

	@Autowired
	public URLConnectionFactory(Proxy proxy) {
		this.proxy = proxy;
	}

	public HttpURLConnection get(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(5000);
		return connection;
	}
}
