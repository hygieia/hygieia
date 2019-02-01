package com.capitalone.dashboard.collector;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fortify")
public class FortifySettings {

	private String cron;
	private List<String> servers;
	private List<String> userNames;
	private List<String> passwords;
	
	public List<String> getServers() {
		return servers;
	}
	
	public void setServers(List<String> servers) {
		this.servers = servers;
	}
	
	public List<String> getUserNames() {
		return userNames;
	}
	
	public void setUserNames(List<String> userNames) {
		this.userNames = userNames;
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
}
