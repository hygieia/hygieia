package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "CaApm")
public class CaApmSettings {

	private String user;
	private String password;
	private String alertWsdl;
	private String modelWsdl;
	private String alertAddress;
	private String modelAddress;
	private String cron;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAlertWsdl() {
		return alertWsdl;
	}

	public void setAlertWsdl(String alertWsdl) {
		this.alertWsdl = alertWsdl;
	}

	public String getModelWsdl() {
		return modelWsdl;
	}

	public void setModelWsdl(String modelWsdl) {
		this.modelWsdl = modelWsdl;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getAlertAddress() {
		return alertAddress;
	}

	public void setAlertAddress(String alertAddress) {
		this.alertAddress = alertAddress;
	}

	public String getModelAddress() {
		return modelAddress;
	}

	public void setModelAddress(String modelAddress) {
		this.modelAddress = modelAddress;
	}

}
