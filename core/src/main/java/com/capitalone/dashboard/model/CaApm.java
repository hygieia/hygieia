package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="ca_apm")
public class CaApm extends BaseModel {
    private int alertCurrStatus;

    private String alertName;

    private int alertPrevStatus;

    private boolean alertStatusChanged;

    private String manModuleName;

    private boolean simpleAlert;
    private int thresholdValue;
    
	public int getAlertCurrStatus() {
		return alertCurrStatus;
	}
	public void setAlertCurrStatus(int alertCurrStatus) {
		this.alertCurrStatus = alertCurrStatus;
	}
	public String getAlertName() {
		return alertName;
	}
	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}
	public int getAlertPrevStatus() {
		return alertPrevStatus;
	}
	public void setAlertPrevStatus(int alertPrevStatus) {
		this.alertPrevStatus = alertPrevStatus;
	}
	public boolean isAlertStatusChanged() {
		return alertStatusChanged;
	}
	public void setAlertStatusChanged(boolean alertStatusChanged) {
		this.alertStatusChanged = alertStatusChanged;
	}
	public String getManModuleName() {
		return manModuleName;
	}
	public void setManModuleName(String manModuleName) {
		this.manModuleName = manModuleName;
	}
	public boolean isSimpleAlert() {
		return simpleAlert;
	}
	public void setSimpleAlert(boolean simpleAlert) {
		this.simpleAlert = simpleAlert;
	}
	public int getThresholdValue() {
		return thresholdValue;
	}
	public void setThresholdValue(int thresholdValue) {
		this.thresholdValue = thresholdValue;
	}
    
    


}
