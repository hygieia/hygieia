package com.capitalone.dashboard.client;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.capitalone.dashboard.util.FeatureSettings;
import com.capitalone.dashboard.util.Supplier;

/**
 * Separate JiraRestClient supplier to make unit testing easier
 * 
 * @author <a href="mailto:MarkRx@users.noreply.github.com">MarkRx</a>
 */
@Component
public class JiraRestClientSupplier implements Supplier<JiraRestClient> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraRestClientSupplier.class);
	
	@Autowired
	private FeatureSettings featureSettings;
	
	@Override
	public JiraRestClient get() {
		JiraRestClient client = null;
		
		String jiraCredentials = featureSettings.getJiraCredentials();
		String jiraBaseUrl = featureSettings.getJiraBaseUrl();
		String proxyUri = null;
		String proxyPort = null;
		
		URI jiraUri = null;
		
		try {
			if (featureSettings.getJiraProxyUrl() != null && !featureSettings.getJiraProxyUrl().isEmpty() && (featureSettings.getJiraProxyPort() != null)) {
				proxyUri = this.featureSettings.getJiraProxyUrl();
				proxyPort = this.featureSettings.getJiraProxyPort();
				
				jiraUri = this.createJiraConnection(jiraBaseUrl,
						proxyUri + ":" + proxyPort, 
						this.decodeCredentials(jiraCredentials).get("username"),
						this.decodeCredentials(jiraCredentials).get("password"));
			} else {
				jiraUri = new URI(jiraBaseUrl);
			}
			
			InetAddress.getByName(jiraUri.getHost());
			client = new AsynchronousJiraRestClientFactory()
					.createWithBasicHttpAuthentication(jiraUri, 
							decodeCredentials(jiraCredentials).get("username"),
							decodeCredentials(jiraCredentials).get("password"));
			
		} catch (UnknownHostException | URISyntaxException e) {
			LOGGER.error("The Jira host name is invalid. Further jira collection cannot proceed.");
			
			LOGGER.debug("Exception", e);
		}
		
		return client;
	}
	
	/**
	 * Converts Jira basic authentication credentials from Base 64 string to a
	 * username/password map
	 * 
	 * @param jiraBasicAuthCredentialsInBase64
	 *            Base64-encoded single string in the following format:
	 *            <em>username:password</em><br/>
	 * <br/>
	 *            A null parameter value will result in an empty hash map
	 *            response (e.g., nothing gets decoded)
	 * @return Decoded username/password map of strings
	 */
	private Map<String, String> decodeCredentials(String jiraBasicAuthCredentialsInBase64) {
		Map<String, String> credMap = new LinkedHashMap<String, String>();
		if (jiraBasicAuthCredentialsInBase64 != null) {
				//the tokenize includes a \n to ensure we trim those off the end (mac base64 adds these!)
			StringTokenizer tokenizer = new StringTokenizer(new String(
					Base64.decodeBase64(jiraBasicAuthCredentialsInBase64)), ":\n");
			for (int i = 0; tokenizer.hasMoreTokens(); i++) {
				if (i == 0) {
					credMap.put("username", tokenizer.nextToken());
				} else {
					credMap.put("password", tokenizer.nextToken());
				}
			}
		}
		
		return credMap;

	}

	/**
	 * Generates an authenticated proxy connection URI and Jira URI for use in
	 * talking to Jira.
	 * 
	 * @param jiraBaseUri
	 *            A string representation of a Jira URI
	 * @param fullProxyUrl
	 *            A string representation of a completed proxy URL:
	 *            http://your.proxy.com:8080
	 * @param username
	 *            A string representation of a username to be authenticated
	 * @param password
	 *            A string representation of a password to be used in
	 *            authentication
	 * @return A fully configured Jira URI with authenticated proxy connection
	 */
	private URI createJiraConnection(String jiraBaseUri, String fullProxyUrl, String username,
			String password) {
		final String uname = username;
		final String pword = password;
		Proxy proxy = null;
		URLConnection connection = null;
		try {
			if (!StringUtils.isEmpty(jiraBaseUri)) {
				URL baseUrl = new URL(jiraBaseUri);
				if (!StringUtils.isEmpty(fullProxyUrl)) {
					URL proxyUrl = new URL(fullProxyUrl);
					URI proxyUri = new URI(proxyUrl.getProtocol(), proxyUrl.getUserInfo(),
							proxyUrl.getHost(), proxyUrl.getPort(), proxyUrl.getPath(),
							proxyUrl.getQuery(), null);
					proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUri.getHost(),
							proxyUri.getPort()));
					connection = baseUrl.openConnection(proxy);

					if (!StringUtils.isEmpty(username) && (!StringUtils.isEmpty(password))) {
						String creds = uname + ":" + pword;
						Authenticator.setDefault(new Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(uname, pword.toCharArray());
							}
						});
						connection.setRequestProperty("Proxy-Authorization",
								"Basic " + Base64.encodeBase64String((creds).getBytes()));
					}
				} else {
					connection = baseUrl.openConnection();
				}
			} else {
				LOGGER.error("The response from Jira was blank or non existant - please check your property configurations");
				return null;
			}

			return connection.getURL().toURI();

		} catch (URISyntaxException | IOException e) {
			try {
				LOGGER.error("There was a problem parsing or reading the proxy configuration settings during openning a Jira connection. Defaulting to a non-proxy URI.");
				return new URI(jiraBaseUri);
			} catch (URISyntaxException e1) {
				LOGGER.error("Correction:  The Jira connection base URI cannot be read!");
				return null;
			}
		}
	}
}
