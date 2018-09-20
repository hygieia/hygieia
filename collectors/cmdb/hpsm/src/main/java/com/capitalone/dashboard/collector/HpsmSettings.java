package com.capitalone.dashboard.collector;

import org.springframework.beans.factory.annotation.Value;
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
    private String changeOrderCron;
    private String incidentCron;
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

	private String envSubType;
	private String envType;

	private String detailsRequestType;
	private String detailsSoapAction;

	private String changeOrderRequestType;
	private String changeOrderSoapAction;

	private String incidentRequestType;
	private String incidentSoapAction;

	private int port;
    private int firstRunHistoryDays;
    private int changeOrderDays;
	private int incidentDays;
	private int incidentOffsetMinutes;
	private int changeOrderOffsetMinutes;

	private String changeOrderQuery;
	private String incidentQuery;
	@Value("${cmdbBatchLimit:500}")
	private String cmdbBatchLimit;
	private String incidentReturnLimit;
	private String changeOrderReturnLimit;

	private String incidentUpdatesRequestType;
	private String incidentUpdatesSoapAction;
	private String incidentUpdatesCron;
	private List<String> incidentEnvironments;

	public List<String> getIncidentEnvironments() { return incidentEnvironments; }

	public void setIncidentEnvironments(List<String> incidentEnvironments) {
		this.incidentEnvironments = incidentEnvironments;
	}

	public int getChangeOrderOffsetMinutes() { return changeOrderOffsetMinutes; }

	public void setChangeOrderOffsetMinutes(int changeOrderOffsetMinutes) {
		this.changeOrderOffsetMinutes = changeOrderOffsetMinutes;
	}

	public int getIncidentOffsetMinutes() { return incidentOffsetMinutes; }

	public void setIncidentOffsetMinutes(int incidentOffsetMinutes) {
		this.incidentOffsetMinutes = incidentOffsetMinutes;
	}

	public String getIncidentUpdatesRequestType() { return incidentUpdatesRequestType; }

	public void setIncidentUpdatesRequestType(String incidentUpdatesRequestType) {
		this.incidentUpdatesRequestType = incidentUpdatesRequestType;
	}

	public String getIncidentUpdatesSoapAction() { return incidentUpdatesSoapAction; }

	public void setIncidentUpdatesSoapAction(String incidentUpdatesSoapAction) {
		this.incidentUpdatesSoapAction = incidentUpdatesSoapAction;
	}

	public String getIncidentUpdatesCron() { return incidentUpdatesCron; }

	public void setIncidentUpdatesCron(String incidentUpdatesCron) {
		this.incidentUpdatesCron = incidentUpdatesCron;
	}

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

	public String getChangeOrderCron() { return changeOrderCron; }

	public void setChangeOrderCron(String changeOrderCron) { this.changeOrderCron = changeOrderCron; }

	public String getIncidentCron() { return incidentCron; }

	public void setIncidentCron(String incidentCron) { this.incidentCron = incidentCron; }

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

	public String getEnvSubType() { return envSubType; }

	public void setEnvSubType(String envSubType) { this.envSubType = envSubType; }

	public String getEnvType() { return envType; }

	public void setEnvType(String envType) { this.envType = envType; }

	public String getChangeOrderRequestType() {	return changeOrderRequestType; 	}

	public void setChangeOrderRequestType(String changeOrderRequestType) { this.changeOrderRequestType = changeOrderRequestType; }

	public String getChangeOrderSoapAction() { return changeOrderSoapAction; }

	public void setChangeOrderSoapAction(String changeOrderSoapAction) { this.changeOrderSoapAction = changeOrderSoapAction; }

	public String getIncidentRequestType() { return incidentRequestType; }

	public void setIncidentRequestType(String incidentRequestType) { this.incidentRequestType = incidentRequestType; }

	public String getIncidentSoapAction() { return incidentSoapAction; }

	public void setIncidentSoapAction(String incidentSoapAction) { this.incidentSoapAction = incidentSoapAction; }

	public int getChangeOrderDays() { return changeOrderDays; }

	public void setChangeOrderDays(int changeOrderDays) { this.changeOrderDays = changeOrderDays; }

	public int getIncidentDays() { return incidentDays; }

	public void setIncidentDays(int incidentDays) { this.incidentDays = incidentDays; }

	public String getChangeOrderQuery() { return changeOrderQuery; }

	public void setChangeOrderQuery(String changeOrderQuery) { this.changeOrderQuery = changeOrderQuery; }

	public String getIncidentQuery() { return incidentQuery; }

	public void setIncidentQuery(String incidentQuery) { this.incidentQuery = incidentQuery; }

	public String getCmdbBatchLimit() {return cmdbBatchLimit;}

	public void setCmdbBatchLimit(String cmdbBatchLimit) {this.cmdbBatchLimit = cmdbBatchLimit;}

	public String getIncidentReturnLimit() { return incidentReturnLimit; }

	public void setIncidentReturnLimit(String incidentReturnLimit) { this.incidentReturnLimit = incidentReturnLimit; }

	public String getChangeOrderReturnLimit() { return changeOrderReturnLimit; }

	public void setChangeOrderReturnLimit(String changeOrderReturnLimit) { this.changeOrderReturnLimit = changeOrderReturnLimit; }
}
