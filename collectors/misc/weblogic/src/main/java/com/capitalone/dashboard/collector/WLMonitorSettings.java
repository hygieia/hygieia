package com.capitalone.dashboard.collector;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * Bean to hold settings specific to the UDeploy collector.
 */
@Component
@ConfigurationProperties(prefix = "weblogic")
public class WLMonitorSettings {
	 	private String cron;
	    private String username;
	    private String password;
	    private List<String> servers;
		public String getCron() {
			return cron;
		}
		public void setCron(String cron) {
			this.cron = cron;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public List<String> getServers() {
			return servers;
		}
		public void setServers(List<String> servers) {
			this.servers = servers;
		} 
}
