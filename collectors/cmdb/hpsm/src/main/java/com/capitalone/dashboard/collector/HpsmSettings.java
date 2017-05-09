package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the HPSM collector.
 */
@Component
@ConfigurationProperties(prefix = "hpsm")
public class HpsmSettings {
    private String cron;
    private String apiServer;
	private String apiProtocol;
	private String apiResource;
	private String apiContentType;
	private String apiCharset;
	private String apiUser;
	private String apiPass;
    private String key;
    private String appSubType;
    private String appType;
    private String appRequestType;
    private String appSoapAction;
	private String compSubType;
	private String compType;
	private String compRequestType;
	private String compSoapAction;
	private List<String> fieldList;
	private int apiPort;
    private int firstRunHistoryDays;


	public String getApiServer() {
		return apiServer;
	}

	public void setApiServer(String apiServer) {
		this.apiServer = apiServer;
	}

	public int getApiPort() {
		return apiPort;
	}

	public void setApiPort(int apiPort) {
		this.apiPort = apiPort;
	}

	public String getApiProtocol() {
		return apiProtocol;
	}

	public void setApiProtocol(String apiProtocol) {
		this.apiProtocol = apiProtocol;
	}

	public String getApiResource() {
		return apiResource;
	}

	public void setApiResource(String apiResource) {
		this.apiResource = apiResource;
	}

	public String getApiContentType() {
		return apiContentType;
	}

	public void setApiContentType(String apiContentType) {
		this.apiContentType = apiContentType;
	}

	public String getApiUser() {
		return apiUser;
	}

	public void setApiUser(String apiUser) {
		this.apiUser = apiUser;
	}

	public String getApiPass() {
		return apiPass;
	}

	public void setApiPass(String apiPass) {
		this.apiPass = apiPass;
	}

	public String getApiCharset() {
		return apiCharset;
	}

	public void setApiCharset(String apiCharset) {
		this.apiCharset = apiCharset;
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

	public String getAppRequestType() {
		return appRequestType;
	}

	public void setAppRequestType(String appRequestType) {
		this.appRequestType = appRequestType;
	}

	public String getAppSoapAction() {
		return appSoapAction;
	}

	public void setAppSoapAction(String appSoapAction) {
		this.appSoapAction = appSoapAction;
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

	public String getCompRequestType() {
		return compRequestType;
	}

	public void setCompRequestType(String compRequestType) {
		this.compRequestType = compRequestType;
	}

	public String getCompSoapAction() {
		return compSoapAction;
	}

	public void setCompSoapAction(String compSoapAction) {
		this.compSoapAction = compSoapAction;
	}

	public List<String> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
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
