package com.capitalone.dashboard.model;

public class PolicyScanMetric {
	
	private Integer policycriticalCount;
    private Integer policysevereCount;
	private Integer polimoderateCount;
	private Integer policyAffectedCount;
	
	public Integer getPolicyAffectedCount() {
		return policyAffectedCount;
	}
	public void setPolicyAffectedCount(Integer policyAffectedCount) {
		this.policyAffectedCount = policyAffectedCount;
	}
	public Integer getPolicycriticalCount() {
		return policycriticalCount;
	}
	public void setPolicycriticalCount(Integer policycriticalCount) {
		this.policycriticalCount = policycriticalCount;
	}
	public Integer getPolicysevereCount() {
		return policysevereCount;
	}
	public void setPolicysevereCount(Integer policysevereCount) {
		this.policysevereCount = policysevereCount;
	}
	public Integer getPolimoderateCount() {
		return polimoderateCount;
	}
	public void setPolimoderateCount(Integer polimoderateCount) {
		this.polimoderateCount = polimoderateCount;
	}

}
