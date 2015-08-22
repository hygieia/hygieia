package com.capitalone.dashboard.datafactory.jira.sdk.connector;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * {@inheritDoc}
 * 
 * @author kfk884
 * 
 */
public class BaseConnectionImpl implements BaseConnection {
	private static final Log LOGGER = LogFactory
			.getLog(BaseConnectionImpl.class);
	protected String proxy;
	protected String port;
	protected String authToken;
	protected String refreshToken;
	protected String redirectUri;
	protected String expireTime;
	protected String credentials;
	protected String baseUrl;
	protected String apiContextPath;

	public BaseConnectionImpl() {
		// Default blank constructor
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.capitalone.jira.client.connector.BaseConnection#setProxy(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public boolean setProxy(String proxy, String port) {
		try {
			if (proxy.isEmpty() || port.isEmpty()) {
				return false;
			}
			this.proxy = proxy;
			this.port = port;
		} catch (NullPointerException npe) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.capitalone.jira.client.connector.BaseConnection#setOAuth(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean setOAuth(String authToken, String refreshToken,
			String redirectUri, String expireTime) {
		try {
			if (authToken.isEmpty() || refreshToken.isEmpty()
					|| redirectUri.isEmpty() || expireTime.isEmpty()) {
				return false;
			}
			this.authToken = authToken;
			this.refreshToken = refreshToken;
			this.redirectUri = redirectUri;
			this.expireTime = expireTime;
		} catch (NullPointerException npe) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.capitalone.jira.client.connector.BaseConnection#setBasicAuth(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public boolean setBasicAuth(String credentials) {
		LOGGER.debug("setBasicAuth: credentials = " + credentials);

		try {
			if (credentials.isEmpty()) {
				return false;
			}
			this.credentials = credentials;
		} catch (NullPointerException npe) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.capitalone.jira.client.connector.BaseConnection#setJiraURI(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public boolean setJiraURI(String baseUrl, String apiContextPath) {
		LOGGER.debug("setJiraURI: baseUrl = " + baseUrl + "; apiContextPath = " + apiContextPath);

		try {
			if (baseUrl.isEmpty() || apiContextPath.isEmpty()) {
				return false;
			}
			this.baseUrl = baseUrl;
			this.apiContextPath = apiContextPath;
		} catch (NullPointerException npe) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.capitalone.jira.client.connector.BaseConnection#generateRequestFactory
	 * ()
	 */
	@Override
	public HttpRequestFactory generateRequestFactory() {
		// Declare local HTTP and Proxy related artifacts
		HttpTransport httpTransport = null;
		HttpRequestFactory requestFactory = null;

		try {
			if (this.proxy == null) {
				httpTransport = new NetHttpTransport();
			} else {
				httpTransport = new NetHttpTransport.Builder().setProxy(
						new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
								this.proxy, Integer.parseInt(this.port))))
						.build();
			}
			requestFactory = httpTransport.createRequestFactory();
			LOGGER.info("Jira HttpRequestFactory has been generated successfully");
		} catch (NullPointerException e) {
			LOGGER.error("There was a problem establishing a base connection to Jira: "
					+ e.getMessage()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
			return requestFactory;
		}

		return requestFactory;
	}

}
