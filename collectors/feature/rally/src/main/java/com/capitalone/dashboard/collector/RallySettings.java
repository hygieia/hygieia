package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the Sonar collector.
 */
@Component
@ConfigurationProperties(prefix = "rally")
public class RallySettings {
    private String cron;
    private List<String> servers;
    private List<String> usernames;
    private List<String> passwords;
    private String httpProxyPort;
    private String httpProxyHost;
    private String httpsProxyPort;
    private String httpsProxyHost;
    
    
    public String getHttpProxyPort() {
		return httpProxyPort;
	}

	public void setHttpProxyPort(String httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}

	public String getHttpProxyHost() {
		return httpProxyHost;
	}

	public void setHttpProxyHost(String httpProxyHost) {
		this.httpProxyHost = httpProxyHost;
	}

	public String getHttpsProxyPort() {
		return httpsProxyPort;
	}

	public void setHttpsProxyPort(String httpsProxyPort) {
		this.httpsProxyPort = httpsProxyPort;
	}

	public String getHttpsProxyHost() {
		return httpsProxyHost;
	}

	public void setHttpsProxyHost(String httpsProxyHost) {
		this.httpsProxyHost = httpsProxyHost;
	}

	public List<String> getUsernames() {
		return usernames;
	}

	public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}

	public List<String> getPasswords() {
		return passwords;
	}

	public void setPasswords(List<String> passwords) {
		this.passwords = passwords;
	}

	public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}
