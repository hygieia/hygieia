package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the HPSM collector.
 */
@Component
@ConfigurationProperties(prefix = "hpsm")
public class HpsmSettings {
    private String cron;
    private String server;
	private String protocol;
	private String resource;
	private String contentType;
	private String charset;
	private String user;
	private String pass;
    private String key;
    private String appSubType;
    private String appType;
    private String appStatus;
	private String compSubType;
	private String compType;

	private String detailsRequestType;
	private String detailsSoapAction;

	private int port;
    private int firstRunHistoryDays;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAppSubType() {
		return appSubType;
	}

	public void setAppSubType(String appSubType) {
		this.appSubType = appSubType;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public String getCompSubType() {
		return compSubType;
	}

	public void setCompSubType(String compSubType) {
		this.compSubType = compSubType;
	}

	public String getCompType() {
		return compType;
	}

	public void setCompType(String compType) {
		this.compType = compType;
	}

	public String getDetailsRequestType() {
		return detailsRequestType;
	}

	public void setDetailsRequestType(String detailsRequestType) {
		this.detailsRequestType = detailsRequestType;
	}

	public String getDetailsSoapAction() {
		return detailsSoapAction;
	}

	public void setDetailsSoapAction(String detailsSoapAction) {
		this.detailsSoapAction = detailsSoapAction;
	}

	public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

    public int getFirstRunHistoryDays() {
		return firstRunHistoryDays;
	}

	public void setFirstRunHistoryDays(int firstRunHistoryDays) {
		this.firstRunHistoryDays = firstRunHistoryDays;
	}
}
